/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.itest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sca4j.featureset.FeatureSet;


/**
 *
 * @version $Revision$ $Date$
 */
public class ArtifactHelper {

    public ArtifactFactory artifactFactory;
    public ArtifactResolver resolver;
    public ArtifactMetadataSource metadataSource;
    
    private MavenProject project;
    private ArtifactRepository localRepository;
    private List<?> remoteRepositories;
    
    /**
     * Sets the local repository to use.
     * 
     * @param localRepository Local repository to use.
     */
    public void setLocalRepository(ArtifactRepository localRepository) {
        this.localRepository = localRepository;
    }
    
    /**
     * Sets the maven project to use.
     * 
     * @param project Maven project to use.
     */
    public void setProject(MavenProject project) {
        this.project = project;
        this.remoteRepositories = project.getRemoteArtifactRepositories();
    }

    public Set<Artifact> calculateRuntimeArtifacts(String runtimeVersion) throws MojoExecutionException {
        
        List<Exclusion> exclusions = Collections.emptyList();
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.sca4j");
        dependency.setArtifactId("sca4j-maven-host");
        dependency.setVersion(runtimeVersion);
        dependency.setExclusions(exclusions);

        return resolveAll(dependency);
        
    }

    /**
     * Calculates module dependencies based on the set of project artifacts. Module dependencies must be visible to implementation code in a composite
     * and encompass project artifacts minus artifacts provided by the host classloader and those that are "provided scope".
     *
     * @param projectArtifacts the artifact set to determine module dependencies from
     * @param hostArtifacts    the set of host artifacts
     * @return the set of URLs pointing to module depedencies.
     */
    public Set<URL> calculateModuleDependencies(Set<Artifact> projectArtifacts, Set<Artifact> hostArtifacts) {
        Set<URL> urls = new LinkedHashSet<URL>();
        for (Artifact artifact : projectArtifacts) {
            try {
                if (hostArtifacts.contains(artifact) || Artifact.SCOPE_PROVIDED.equals(artifact.getScope())) {
                    continue;
                }
                File pathElement = artifact.getFile();
                URL url = pathElement.toURI().toURL();
                urls.add(url);

            } catch (MalformedURLException e) {
                // toURI should have encoded the URL
                throw new AssertionError(e);
            }

        }
        return urls;
    }

    public Set<Artifact> calculateDependencies() throws MojoExecutionException {
        // add all declared project dependencies
        Set<Artifact> artifacts = new HashSet<Artifact>();
        List<?> dependencies = project.getDependencies();
        for (int i = 0;i < dependencies.size();i++) {
            Dependency dependency = (Dependency) dependencies.get(i);
            artifacts.addAll(resolveAll(dependency));
        }

        // include any artifacts that have been added by other plugins (e.g. Clover see FABRICTHREE-220)
        Iterator<?> it = project.getDependencyArtifacts().iterator();
        while (it.hasNext()) {
        	artifacts.add((Artifact) it.next());
        }
        return artifacts;
    }

    /**
     * Transitively calculates the set of artifacts to be included in the host classloader based on the artifacts associated with the Maven module.
     *
     * @param runtimeArtifacts the artifacts associated with the Maven module
     * @return set of artifacts to be included in the host classloader
     * @throws MojoExecutionException if an error occurs calculating the transitive dependencies
     */
    public Set<Artifact> calculateHostArtifacts(Set<Artifact> runtimeArtifacts, Dependency[] shared, List<FeatureSet> featureSets) throws MojoExecutionException {
        
        Set<Artifact> hostArtifacts = new HashSet<Artifact>();
        List<Exclusion> exclusions = Collections.emptyList();
        // find the version of sca4j-api being used by the runtime
        String version = null;
        for (Artifact artifact : runtimeArtifacts) {
            if ("org.sca4j".equals(artifact.getGroupId()) && "sca4j-api".equals(artifact.getArtifactId())) {
                version = artifact.getVersion();
                break;
            }
        }
        if (version == null) {
            throw new MojoExecutionException("org.sca4j:sca4j-api version not found");
        }
        // add transitive dependencies of sca4j-api to the list of artifacts in the host classloader
        Dependency sca4jApi = new Dependency();
        sca4jApi.setGroupId("org.sca4j");
        sca4jApi.setArtifactId("sca4j-api");
        sca4jApi.setVersion(version);
        sca4jApi.setExclusions(exclusions);
        hostArtifacts.addAll(resolveAll(sca4jApi));

        // add commons annotations dependency
        Dependency jsr250API = new Dependency();
        jsr250API.setGroupId("org.apache.geronimo.specs");
        jsr250API.setArtifactId("geronimo-annotation_1.0_spec");
        jsr250API.setVersion("1.1");
        jsr250API.setExclusions(exclusions);
        hostArtifacts.addAll(resolveAll(jsr250API));

        // add shared artifacts to the host classpath
        if (shared != null) {
            for (Dependency sharedDependency : shared) {
                hostArtifacts.addAll(resolveAll(sharedDependency));
            }
        }
        
        for (FeatureSet featureSet : featureSets) {
        	for (Dependency sharedLibrary : featureSet.getSharedLibraries()) {
                hostArtifacts.addAll(resolveAll(sharedLibrary));
        		
        	}
        }
        return hostArtifacts;
    }
    
    /**
     * Resolves the root dependency to the local artifact.
     * 
     * @param dependency Root dependency.
     * @return Resolved artifact.
     * @throws MojoExecutionException if unable to resolve any dependencies.
     */
    public Artifact resolve(Dependency dependency) throws MojoExecutionException {
        return  resolveArtifacts(dependency, false).iterator().next();
    }
    
    /**
     * Resolves all the dependencies transitively to local artifacts.
     * 
     * @param dependency Root dependency.
     * @return Resolved set of artifacts.
     * @throws MojoExecutionException if unable to resolve any dependencies.
     */
    private Set<Artifact> resolveAll(Dependency dependency) throws MojoExecutionException {
        return resolveArtifacts(dependency, true);
    }
    
    private Set<Artifact> resolveArtifacts(Dependency dependency, boolean transitive) throws MojoExecutionException {
        
        Set<Artifact> artifacts = new HashSet<Artifact>();

        if (dependency.getVersion() == null) {
            resolveDependencyVersion(dependency);
        }
        final List<?> exclusions = dependency.getExclusions();

        Artifact rootArtifact = createArtifact(dependency);
        
        try {
            
            resolver.resolve(rootArtifact, remoteRepositories, localRepository);
            artifacts.add(rootArtifact);
            
            if (!transitive) {
                return artifacts;
            }
            
            ResolutionGroup resolutionGroup = metadataSource.retrieve(rootArtifact, localRepository, remoteRepositories);
            ArtifactFilter filter = new ArtifactFilter() {

                public boolean include(Artifact artifact) {
                    String groupId = artifact.getGroupId();
                    String artifactId = artifact.getArtifactId();

                    for (int i = 0; i < exclusions.size();i++) {
                        Exclusion exclusion = (Exclusion) exclusions.get(i);
                        if (artifactId.equals(exclusion.getArtifactId()) && groupId.equals(exclusion.getGroupId())) {
                            return false;
                        }
                    }
                    return true;
                }

            };
            ArtifactResolutionResult result = resolver.resolveTransitively(resolutionGroup.getArtifacts(),
                                                                           rootArtifact,
                                                                           Collections.emptyMap(),
                                                                           localRepository,
                                                                           remoteRepositories,
                                                                           metadataSource,
                                                                           filter);
            Iterator<?> resolvedArtifacts = result.getArtifacts().iterator();
            
            while (resolvedArtifacts.hasNext()) {
                artifacts.add((Artifact) resolvedArtifacts.next());
            }
            
            return artifacts;
            
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ArtifactMetadataRetrievalException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
    }
    
    /*
     * Resolves the dependency version from the projects managed dependencies.
     */
    private void resolveDependencyVersion(Dependency dependency) {

        List<?> managedDependencies = project.getDependencyManagement().getDependencies();
        for ( int i = 0;i < managedDependencies.size();i++) {
            Dependency managedDependency = (Dependency) managedDependencies.get(i);
            if (managedDependency.getGroupId().equals(dependency.getGroupId())
                    && managedDependency.getArtifactId().equals(dependency.getArtifactId())) {
                dependency.setVersion(managedDependency.getVersion());

            }
        }
    }

    /*
     * Creates an artifact from the dependency.
     */
    private Artifact createArtifact(Dependency dependency) {
        return artifactFactory.createArtifact(dependency.getGroupId(),
                                              dependency.getArtifactId(),
                                              dependency.getVersion(),
                                              Artifact.SCOPE_RUNTIME,
                                              dependency.getType());
    }

}
