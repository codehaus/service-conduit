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

package org.sca4j.jetty.plugin.impl;

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
import org.apache.maven.plugin.MojoFailureException;
import org.mortbay.jetty.plugin.Jetty6RunMojo;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 *
 * Mojo to setup the SCA4J extensions and boot, so that the source can be deployed on Jetty Server.
 *
 * @extendsPlugin maven-jetty-plugin
 * @goal run
 * @requiresDependencyResolution runtime
 * @execute phase="test-compile"
 * @description Runs jetty6 directly from a maven project
 */
public class SCA4JJettyRunMojo extends Jetty6RunMojo {
    /**
     * The lib path, used to output the f3Extensions.properties and f3UserExtensions.properties files.
     */
    private static final String LIB_PATH = "\\WEB-INF\\lib";

	/**
	 * The version of the runtime to use.
	 *
	 * @parameter expression="0.5"
	 */
	public String runTimeVersion;

	/**
	 * Set of extension artifacts that should be deployed to the runtime.
	 *
	 * @parameter
	 */
	public Dependency[] extensions;

    /**
	 * Set of user extension artifacts that should be deployed to the runtime.
	 *
	 * @parameter
	 */
	public Dependency[] userExtensions;

    /**
	 * Set of extension artifacts that should be deployed to the runtime.
	 *
	 * @parameter
	 */
	public Dependency[] shared;

	/**
	 * Used to look up Artifacts in the remote repository.
	 *
	 * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
	 * @required
	 * @readonly
	 */
	public ArtifactResolver resolver;

	/**
	 * Used to look up Artifacts in the remote repository.
	 *
	 * @parameter expression="${component.org.apache.maven.artifact.metadata.ArtifactMetadataSource}"
	 * @required
	 * @readonly
	 */
	public ArtifactMetadataSource metadataSource;

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
	public List remoteRepositories;

	/**
	 * Used to look up Artifacts in the remote repository.
	 *
	 * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
	 * @required
	 * @readonly
	 */
	public ArtifactFactory artifactFactory;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mortbay.jetty.plugin.Jetty6RunMojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
	}

	/**
	 * Overriden from the base class, so as to set up the Boot and extensions.
	 * @see org.mortbay.jetty.plugin.AbstractJettyRunMojo#getClassPathFiles()
	 */
	@Override
	public List getClassPathFiles() {
        // Ensure that we have a target directory for our various properties files
        File libTargetPath = new File(getWebAppSourceDirectory(), LIB_PATH);
        if (!libTargetPath.exists()) {
            libTargetPath.mkdirs();
        }

        try {
			List<File> list = super.getClassPathFiles();
			list.addAll(setUpBootRuntime());
			list.addAll(setUpExtensions(extensions, libTargetPath, new File(libTargetPath, "f3Extensions.properties")));
            list.addAll(setUpExtensions(userExtensions, libTargetPath, new File(libTargetPath, "f3UserExtensions.properties")));
            list.addAll(setUpShared());
            return list;
		} catch (MojoExecutionException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	/**
	 * Sets up the extension directory
	 *
	 * @throws MojoExecutionException
	 */
	private List<File> setUpExtensions(Dependency[] extensions, File libTarget, File propertiesTarget)
            throws MojoExecutionException {
		List<File> extensionLibs = new ArrayList<File>();
        List<Artifact> extensionArtifacts = new ArrayList<Artifact>();
        resolveExtensions(extensionLibs, extensionArtifacts, extensions);

        // Copy the libraries into place
        Iterator<File> libIterator = extensionLibs.iterator();
		while (libIterator.hasNext()) {
			File lib = libIterator.next();

            try {
                FileUtils.copyFile(lib, new File(libTarget, lib.getName()));
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to copy library " + lib.getName(), ex);
            }
        }

        // Generate the properties file
        Properties props = new Properties();
        Iterator<Artifact> artifactIt = extensionArtifacts.iterator();
        while (artifactIt.hasNext()) {
            Artifact a = artifactIt.next();

            props.put(a.getFile().getName(), a.getFile().getName());
        }

        try {
            FileOutputStream fos = new FileOutputStream(propertiesTarget);
            props.store(fos, "Generated by SCA4J Jetty Plugin");
            fos.close();
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to generate properties file " + propertiesTarget.getName(), ex);
        }

        return extensionLibs;
    }

	/**
	 * Sets up the boot runitme.
	 * @throws MojoExecutionException
	 */
	private List<File> setUpBootRuntime() throws MojoExecutionException {
		List<File> bootLibs = new ArrayList<File>();
		addWebappRuntime(bootLibs);

        return bootLibs;
    }

    /**
	 * Sets up the shared dependencies.
	 * @throws MojoExecutionException
	 */
	private List<File> setUpShared() throws MojoExecutionException {
		List<File> sharedLibs = new ArrayList<File>();

        for (Dependency dep : shared) {
            resolveDependency(sharedLibs, dep);
        }

        return sharedLibs;
    }

    /**
	 * Method to add the sca4j-webapp-host dependency on the Boot Classpath.
	 *
	 * @param classpath
	 * @throws MojoExecutionException
	 */
	private void addWebappRuntime(List<File> classpath)
			throws MojoExecutionException {
		Set<Artifact> artifacts = new HashSet<Artifact>();
		List<Exclusion> exclusions = Collections.emptyList();
		Dependency dependency = new Dependency();
		dependency.setGroupId("org.sca4j.webapp");
		dependency.setArtifactId("sca4j-webapp-host");
		dependency.setVersion(runTimeVersion);
		dependency.setExclusions(exclusions);
		addArtifacts(artifacts, dependency);

		Iterator<Artifact> artifactIterator = artifacts.iterator();

		while (artifactIterator.hasNext()) {
			Artifact artifact = artifactIterator.next();
			classpath.add(artifact.getFile());
		}
	}

	/**
	 * Resolves the dependency version, in case the versions are provided in the dependencyManagement of parent POM.
	 * @param extension
	 */
	private void resolveDependencyVersion(Dependency extension) {

		List<org.apache.maven.model.Dependency> dependencies = getProject()
				.getDependencyManagement().getDependencies();
		for (org.apache.maven.model.Dependency dependecy : dependencies) {
			if (dependecy.getGroupId().equals(extension.getGroupId())
					&& dependecy.getArtifactId().equals(
							extension.getArtifactId())) {
				extension.setVersion(dependecy.getVersion());

			}
		}
	}

	/**
	 * Resolves the dependency transitively and adds to the current artifacts List.
	 * @param artifacts list to which the dependencies will be added.
	 * @param extension dependency to resolve.
	 * @throws MojoExecutionException
	 */
	private void addArtifacts(Set<Artifact> artifacts, Dependency extension)
			throws MojoExecutionException {

		if (extension.getVersion() == null) {
			resolveDependencyVersion(extension);
		}
		final List<Exclusion> exclusions = extension.getExclusions();

		Artifact artifact = createArtifact(extension);
		try {
			resolver.resolve(artifact, remoteRepositories, localRepository);
			ResolutionGroup resolutionGroup = metadataSource.retrieve(artifact,
					localRepository, remoteRepositories);
			ArtifactFilter filter = new ArtifactFilter() {

				public boolean include(Artifact artifact) {
					String groupId = artifact.getGroupId();
					String artifactId = artifact.getArtifactId();

					for (Exclusion exclusion : exclusions) {
						if (artifactId.equals(exclusion.getArtifactId())
								&& groupId.equals(exclusion.getGroupId())) {
							return false;
						}
					}
					return true;
				}

			};
			ArtifactResolutionResult result = resolver.resolveTransitively(
					resolutionGroup.getArtifacts(), artifact, Collections
							.emptyMap(), localRepository, remoteRepositories,
					metadataSource, filter);
			@SuppressWarnings("unchecked")
			Set<Artifact> resolvedArtifacts = result.getArtifacts();
			artifacts.add(artifact);
			artifacts.addAll(resolvedArtifacts);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ArtifactNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (ArtifactMetadataRetrievalException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	/**
	 *  Creates an artifact in the repository.
	 * @param dependency
	 * @return
	 */
	private Artifact createArtifact(Dependency dependency) {
		return artifactFactory.createArtifact(dependency.getGroupId(),
				dependency.getArtifactId(), dependency.getVersion(),
				Artifact.SCOPE_RUNTIME, dependency.getType());
	}

	/**
	 * Add extensions to the ClassPath.
	 * @param classPathFiles
	 * @throws MojoExecutionException
	 */
	private void resolveExtensions(List<File> classPathFiles, List<Artifact> extensionArtifacts,
                               Dependency[] configuredExtensions)
			throws MojoExecutionException {
		if (configuredExtensions == null) {
			return;
		}

		for (Dependency dependency : configuredExtensions) {
            // Resolve the version if necessary
            if (dependency.getVersion() == null) {
                resolveDependencyVersion(dependency);
            }

            // Resolve and create the artifact
            Artifact dependencyArtifact = createArtifact(dependency);
            try {
                resolver.resolve(dependencyArtifact, remoteRepositories, localRepository);
                extensionArtifacts.add(dependencyArtifact);
            } catch (ArtifactResolutionException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            } catch (ArtifactNotFoundException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }

            resolveDependency(classPathFiles, dependency);
		}
	}

    private void resolveDependency(List<File> classPathFiles, Dependency dependency) throws MojoExecutionException {
        if (dependency.getVersion() == null) {
            resolveDependencyVersion(dependency);
        }
        Set<Artifact> artifacts = new HashSet();
        addArtifacts(artifacts, dependency);

        for (Artifact a : artifacts) {
            classPathFiles.add(a.getFile());
        }

        /*Artifact artifact = createArtifact(dependency);
        try {
            resolver.resolve(artifact, remoteRepositories, localRepository);
            classPathFiles.add(artifact.getFile());
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }*/
    }
}
