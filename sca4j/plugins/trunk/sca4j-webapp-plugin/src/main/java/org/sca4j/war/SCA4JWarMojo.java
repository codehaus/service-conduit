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
package org.sca4j.war;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

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
     * @parameter expression="${component.org.sca4j.war.ArtifactHelper}"
     * @required
     * @readonly
     */
    public ArtifactHelper artifactHelper;

    /**
     * The directory where the webapp is built.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    public File webappDirectory;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    public ArtifactRepository localRepository;

    /**
     * Set of extension artifacts that should be deployed to the runtime.
     *
     * @parameter
     */
    public Dependency[] extensions;

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

        artifactHelper.setLocalRepository(localRepository);
        artifactHelper.setProject(project);
        
        Dependency bootLib = new Dependency();
        bootLib.setGroupId("org.sca4j.webapp");
        bootLib.setArtifactId("sca4j-webapp-host");
        bootLib.setVersion(runTimeVersion);

        File bootDir = new File(webappDirectory, BOOT_PATH);
        bootDir.mkdirs();
        for (Artifact artifact : artifactHelper.resolveArtifacts(bootLib, true)) {
            FileUtils.copyFileToDirectoryIfModified(artifact.getFile(), bootDir);
        }
        
    }

    private void installExtensions() throws MojoExecutionException {

        try {
            
            Properties props = new Properties();
            File extensionsDir = new File(webappDirectory, EXTENSIONS_PATH);

            // process Maven dependencies
            for (Dependency extension : extensions) {
                Artifact extensionArtifact = artifactHelper.resolve(extension);
                for (Artifact artifact : artifactHelper.resolveArtifacts(extension, true)) {
                    String extractedLibraryName = artifact.getFile().getName();
                    File extractedLibraryFile = new File(extensionsDir, extractedLibraryName);
                    if (!extractedLibraryFile.exists()) {
                        FileOutputStream outputStream = new FileOutputStream(extractedLibraryFile);
                        InputStream inputStream = new FileInputStream(artifact.getFile());
                        IOUtil.copy(inputStream, outputStream);
                        IOUtil.close(inputStream);
                        IOUtil.close(outputStream);
                    }
                }
                props.put(extensionArtifact.getFile().getName(), extensionArtifact.getFile().getName());
            }

            props.store(new FileOutputStream(new File(extensionsDir, "f3Extensions.properties")), null);

        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }
    
}
