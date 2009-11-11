/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
package org.sca4j.war;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
    
    public Set<Artifact> resolveArtifacts(Dependency dependency, boolean transitive) throws MojoExecutionException {
        
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
