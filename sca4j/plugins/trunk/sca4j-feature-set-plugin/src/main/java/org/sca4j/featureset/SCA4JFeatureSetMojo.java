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
package org.sca4j.featureset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;

/**
 * 
 * Mojo for generating a feature set from a set of requested extensions. A feature set can be built by composing a number of other feature sets, 
 * and or including a set of explicitly requested extensions. A feature set is published as maven artifact with the extension .xml. This can be later 
 * referenced by the itest and webapp plugins, instead of explictly referencing all the extensions included in the feature set. User applications are 
 * expected to have a separate maven module to build the feature set, and then the installed artifact will be reused from the other modules that use 
 * the itest and webapp plugins. Feature sets can also contain shared dependencies used in itest environments.
 * 
 * An example usage of the feature set plugin is shown below,
 * 
 * <pre>
 *    &lt;plugin&gt;
 *       &lt;groupId&gt;org.sca4j&lt;/groupId&gt;
 *       &lt;artifactId&gt;sca4j-feature-set-plugin&lt;/artifactId&gt;
 *       &lt;extensions&gt;true&lt;/extensions&gt;
 *       &lt;configuration&gt;
 *          &lt;extensions&gt;
 *             &lt;dependency&gt;
 *                &lt;groupId&gt;org.mycompanyf&lt;/groupId&gt;
 *                &lt;artifactId&gt;mycompany-extension&lt;/artifactId&gt;
 *             &lt;/dependency&gt;
 *          &lt;/extensions&gt;
 *          &lt;includes&gt;
 *             &lt;dependency&gt;
 *                &lt;groupId&gt;org.sca4j&lt;/groupId&gt;
 *                &lt;artifactId&gt;sca4j-hibernate-feature-set&lt;/artifactId&gt;
 *             &lt;/dependency&gt;
 *          &lt;/includes&gt;
 *          &lt;shared&gt;
 *             &lt;dependency&gt;
 *                &lt;groupId&gt;javax.persistence&lt;/groupId&gt;
 *                &lt;artifactId&gt;persistence-api&lt;/artifactId&gt;
 *             &lt;/dependency&gt;
 *          &lt;/shared&gt;
 *       &lt;/configuration&gt;
 *     &lt;/plugin&gt;
 * </pre>
 *
 * @version $Revision$ $Date$
 * @goal package
 * @phase package
 */
public class SCA4JFeatureSetMojo extends AbstractMojo {

    /**
     *
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    /**
     * @parameter
     */
    protected Dependency[] extensions;

    /**
     * Build output directory.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * @parameter
     */
    protected Dependency[] includes;

    /**
     * @parameter
     */
    protected Dependency[] shared;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    public ArtifactResolver resolver;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    public ArtifactRepository localRepository;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    public List<String> remoteRepositories;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    public ArtifactFactory artifactFactory;

    // Feature set containing the requested extensions
    private FeatureSet featureSet = new FeatureSet();

    /**
     * Generates the feature set files.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        String fileName = project.getArtifactId() + "-" + project.getVersion() + ".xml";
        File file = new File(outputDirectory, fileName);
        try {
            outputDirectory.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        if (extensions == null && includes == null) {
            throw new MojoExecutionException("Extensions or includes should be specified");
        }

        processExtensions();
        processShared();
        processIncludes();
        
        try {
            featureSet.serialize(file);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        project.getArtifact().setFile(file);

    }

    /*
     * Processes the included feature sets.
     */
    private void processIncludes() throws MojoExecutionException {
        
        if (includes == null) {
            return;
        }

        for (Dependency include : includes) {
            
            File featureSetFile = resolve(include);
            FeatureSet includedFeatureSet = null;
			try {
				includedFeatureSet = FeatureSet.deserialize(featureSetFile);
			} catch (ParserConfigurationException e) {
				throw new MojoExecutionException("Unable to process includes", e);
			} catch (SAXException e) {
				throw new MojoExecutionException("Unable to process includes", e);
			} catch (IOException e) {
				throw new MojoExecutionException("Unable to process includes", e);
			}
            
            for (org.apache.maven.model.Dependency extension : includedFeatureSet.getExtensions()) {
            	resolve(extension);
                featureSet.addExtension(extension);
            }
            
            for (org.apache.maven.model.Dependency sharedLibrary : includedFeatureSet.getSharedLibraries()) {
            	resolve(sharedLibrary);
                featureSet.addSharedLibrary(sharedLibrary);
            }
            
        }
        
    }

    /*
     * Processes the requested extensions. 
     */
    private void processExtensions() throws MojoExecutionException {
        
        if (extensions == null) {
            return;
        }
        
        for (Dependency extension : extensions) {
            resolve(extension);
            featureSet.addExtension(extension);
        }
        
    }

    /*
     * Processes the requested shared libraries. 
     */
    private void processShared() throws MojoExecutionException {
        
        if (shared == null) {
            return;
        }
        
        for (Dependency sharedLibrary : shared) {
            resolve(sharedLibrary);
            featureSet.addSharedLibrary(sharedLibrary);
        }
        
    }

    /*
     * Resolves the depnednecy to anartifact file in the repository.
     */
    private File resolve(org.apache.maven.model.Dependency dep) throws MojoExecutionException {

        if (dep.getVersion() == null) {
            resolveDependencyVersion(dep);
        }

        Artifact artifact = createArtifact(dep);
        try {
            resolver.resolve(artifact, remoteRepositories, localRepository);
            return artifact.getFile();
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /*
     * Creates the artifact from the dependency.
     */
    private Artifact createArtifact(org.apache.maven.model.Dependency dep) {
        return artifactFactory.createArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), Artifact.SCOPE_RUNTIME, dep.getType());
    }

    /*
     * Resolves the dependency version, if the version is not specified.
     */
    @SuppressWarnings("unchecked")
    private void resolveDependencyVersion(org.apache.maven.model.Dependency dep) {

        List<org.apache.maven.model.Dependency> dependencies = project.getDependencyManagement().getDependencies();
        for (org.apache.maven.model.Dependency dependecy : dependencies) {
            if (dependecy.getGroupId().equals(dep.getGroupId()) && dependecy.getArtifactId().equals(dep.getArtifactId())) {
                dep.setVersion(dependecy.getVersion());

            }
        }

    }

}
