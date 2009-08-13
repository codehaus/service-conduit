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
package org.sca4j.war;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Add sca4j runtime dependencies to a webapp. The wenapp runtime in SCA4J currently doesn't support classloader isolation. All JAR files are
 * added to the WEB-INF/lib directory. All system and user extensions are added to the same directory as well. The list of system extensions are
 * specified in the properties file f3Extensions.properties and the list of user extensions are specified in the f3UserExtenion.properties.
 * <p/>
 * Both system and user extensions are exploded and the contents of the META-INF/lib directory are copied to the WEB-INF/lib directory.
 * <p/>
 * <p/>
 * Performs the following tasks.
 * <p/>
 * <ul> <li>Adds the boot dependencies transitively to WEB-INF/lib</li> <li>By default boot libraries are transitively resolved from webapp-host</li>
 * <li>The version of boot libraries can be specified using configuration/runTimeVersion element</li> <li>Boot libraries can be overridden using the
 * configuration/bootLibs element in the plugin</li> <li>Adds the extension artifacts specified using configuration/extensions to WEB-INF/lib</li>
 * </ul>
 *
 * @version $Rev: 5398 $ $Date: 2008-09-13 15:22:14 +0100 (Sat, 13 Sep 2008) $
 * @goal sca4j-war
 * @phase generate-resources
 */
public class SCA4JWarMojo extends AbstractMojo {

    /**
     * SCA4J boot path.
     */
    private static final String BOOT_PATH = "WEB-INF/lib";

    /**
     * SCA4J extensions path.
     */
    private static final String EXTENSIONS_PATH = "WEB-INF/lib";

    /**
     * SCA4J user extensions path.
     */
    private static final String USER_EXTENSIONS_PATH = "WEB-INF/lib";
    
    /**
     * Exclude any embedded dependencies from extensions.
     * 
     * @parameter
     */
    public List<String> excludes = new LinkedList<String>();

    /**
     * The directory where the webapp is built.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    public File webappDirectory;

    /**
     * Artifact metadata source.
     *
     * @component
     */
    public ArtifactMetadataSource metadataSource;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    public ArtifactFactory artifactFactory;

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
    public List remoteRepositories;

    /**
     * The directory for the generated WAR.
     *
     * @parameter
     */
    public Dependency[] bootLibs;

    /**
     * Set of extension artifacts that should be deployed to the runtime.
     *
     * @parameter
     */
    public Dependency[] extensions;

    /**
     * Set of extension artifacts that should be deployed to the runtime expressed as feature sets.
     *
     * @parameter
     */
    public Dependency[] features;

    /**
     * Whether to exclude default features.
     *
     * @parameter
     */
    public boolean excludeDefaultFeatures;

    /**
     * The default version of the runtime to use.
     *
     * @parameter expression="RELEASE"
     */
    public String runTimeVersion;

    /**
     * POM
     *
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    public MavenProject project;

    private DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    public SCA4JWarMojo() throws ParserConfigurationException {
    }

    /**
     * Executes the MOJO.
     */
    public void execute() throws MojoExecutionException {
        try {
            installRuntime();
            installExtensions();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void installRuntime() throws MojoExecutionException, IOException {

        getLog().info("Using sca4j runtime version " + runTimeVersion);

        if (bootLibs == null) {
            Dependency dependancy = new Dependency("org.sca4j.webapp", "sca4j-webapp-host", runTimeVersion);
            bootLibs = new Dependency[]{dependancy};
        }

        File bootDir = new File(webappDirectory, BOOT_PATH);
        bootDir.mkdirs();
        for (Dependency dependency : bootLibs) {
            if (dependency.getVersion() == null) {
                resolveDependencyVersion(dependency);
            }
            for (Artifact artifact : resolveArtifact(dependency.getArtifact(artifactFactory), true)) {
                FileUtils.copyFileToDirectoryIfModified(artifact.getFile(), bootDir);
            }
        }
    }

    private void installExtensions() throws MojoExecutionException {

        try {

            Set<Dependency> uniqueExtensions = new HashSet<Dependency>();
            if (extensions != null) {
                for (Dependency extension : extensions) {
					if (extension.getVersion() == null) {
					  resolveDependencyVersion(extension);
                    }
                    uniqueExtensions.add(extension);
                }
            }
            
            List<Dependency> featuresToInstall = getFeaturesToInstall();
            
            if (!featuresToInstall.isEmpty()) {
                for (Dependency feature : featuresToInstall) {
                    if (feature.getVersion() == null) {
                        resolveDependencyVersion(feature);
                    }
                    Artifact featureArtifact = feature.getArtifact(artifactFactory);
                    featureArtifact = resolveArtifact(featureArtifact, false).iterator().next();

                    Document featureSetDoc = db.parse(featureArtifact.getFile());

                    NodeList extensionList = featureSetDoc.getElementsByTagName("extension");

                    for (int i = 0; i < extensionList.getLength(); i++) {

                        Element extensionElement = (Element) extensionList.item(i);

                        Element artifactIdElement = (Element) extensionElement.getElementsByTagName("artifactId").item(0);
                        Element groupIdElement = (Element) extensionElement.getElementsByTagName("groupId").item(0);
                        Element versionElement = (Element) extensionElement.getElementsByTagName("version").item(0);

                        Dependency extension =
                            new Dependency(groupIdElement.getTextContent(), artifactIdElement.getTextContent(), versionElement.getTextContent());

                        uniqueExtensions.add(extension);

                    }
                }
            }
            processExtensions(EXTENSIONS_PATH, "f3Extensions.properties", uniqueExtensions);
            uniqueExtensions.clear();

        } catch(SAXParseException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

	private List<Dependency> getFeaturesToInstall() {
		List<Dependency> featuresToInstall = new ArrayList<Dependency>();
		
		if (features != null) {
			featuresToInstall.addAll(Arrays.asList(features));
		}
		if (!excludeDefaultFeatures) {
			Dependency dependency = new Dependency("org.sca4j", "sca4j-default-feature", runTimeVersion);
			dependency.setType("xml");
			featuresToInstall.add(dependency);
		}
		return featuresToInstall;
	}

    private void processExtensions(String extenstionsPath, String extensionProperties, Set<Dependency> extensions) throws MojoExecutionException {

        try {
            Properties props = new Properties();
            File extensionsDir = new File(webappDirectory, extenstionsPath);

            // process Maven dependencies
            for (Dependency dependency : extensions) {

                if (dependency.getVersion() == null) {
                    resolveDependencyVersion(dependency);
                }

                Artifact extensionArtifact = dependency.getArtifact(artifactFactory);
                extensionArtifact = resolveArtifact(extensionArtifact, false).iterator().next();

                File deflatedExtensionFile = new File(extensionsDir, extensionArtifact.getFile().getName());
                JarOutputStream deflatedExtensionOutputStream = new JarOutputStream(new FileOutputStream(deflatedExtensionFile));

                JarFile extensionFile = new JarFile(extensionArtifact.getFile());
                Enumeration<JarEntry> entries = extensionFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    if (entryName.startsWith("META-INF/lib") && entryName.endsWith(".jar")) {
                        String extractedLibraryName = entryName.substring(entryName.lastIndexOf('/') + 1);
                        if (excludes.contains(extractedLibraryName)) {
                            continue;
                        }
                        File extractedLibraryFile = new File(extensionsDir, extractedLibraryName);
                        if (!extractedLibraryFile.exists()) {
                            FileOutputStream outputStream = new FileOutputStream(extractedLibraryFile);
                            InputStream inputStream = extensionFile.getInputStream(jarEntry);
                            IOUtil.copy(inputStream, outputStream);
                            IOUtil.close(inputStream);
                            IOUtil.close(outputStream);
                        }
                    } else {
                        deflatedExtensionOutputStream.putNextEntry(jarEntry);
                        InputStream inputStream = extensionFile.getInputStream(jarEntry);
                        IOUtil.copy(inputStream, deflatedExtensionOutputStream);
                        IOUtil.close(inputStream);
                    }
                }

                IOUtil.close(deflatedExtensionOutputStream);

                props.put(extensionArtifact.getFile().getName(), extensionArtifact.getFile().getName());

            }


            props.store(new FileOutputStream(new File(extensionsDir, extensionProperties)), null);

        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

    /**
     * Resolve the dependency for the given extension from the dependencyManagement from the pom
     *
     * @param extension the dependcy information for the extension
     */
    private void resolveDependencyVersion(Dependency extension) {
        List<org.apache.maven.model.Dependency> dependencies = project.getDependencyManagement().getDependencies();
        for (org.apache.maven.model.Dependency dependecy : dependencies) {
            if (dependecy.getGroupId().equals(extension.getGroupId())
                    && dependecy.getArtifactId().equals(extension.getArtifactId())) {
                extension.setVersion(dependecy.getVersion());

            }
        }
    }

    /**
     * Resolves the specified artifact.
     *
     * @param artifact   Artifact to be resolved.
     * @param transitive Whether to resolve transitively.
     * @return A set of resolved artifacts.
     * @throws IOException                 In case of an unexpected IO error.
     * @throws ArtifactResolutionException If the artifact cannot be resolved.
     * @throws ArtifactNotFoundException   If the artifact is not found.
     * @throws ArtifactMetadataRetrievalException
     *                                     In case of error in retrieving metadata.
     */
    private Set<Artifact> resolveArtifact(Artifact artifact, boolean transitive) throws MojoExecutionException {

        try {

            Set<Artifact> resolvedArtifacts = new HashSet<Artifact>();

            // Resolve the artifact
            resolver.resolve(artifact, remoteRepositories, localRepository);
            resolvedArtifacts.add(artifact);

            if (!transitive) {
                return resolvedArtifacts;
            }

            // Transitively resolve all the dependencies
            ResolutionGroup resolutionGroup = metadataSource.retrieve(artifact, localRepository, remoteRepositories);
            ArtifactResolutionResult result = resolver.resolveTransitively(resolutionGroup.getArtifacts(),
                                                                           artifact,
                                                                           remoteRepositories,
                                                                           localRepository,
                                                                           metadataSource);

            // Add the artifacts to the deployment unit
            for (Object depArtifact : result.getArtifacts()) {
                resolvedArtifacts.add((Artifact) depArtifact);
            }
            return resolvedArtifacts;

        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ArtifactMetadataRetrievalException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }
}
