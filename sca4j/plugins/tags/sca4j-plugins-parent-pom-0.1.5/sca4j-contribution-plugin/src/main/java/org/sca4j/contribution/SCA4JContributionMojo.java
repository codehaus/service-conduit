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
package org.sca4j.contribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Builds an SCA contribution. Two contribution types are currently supported, ZIP- and XML-based.
 * <p/>
 * Contribution archives may be jars or zip files as specified by the respective Maven packaging entries,
 * <code>&lt;packaging&gt;sca-contribution-jar&lt;/packaging&gt;</code> and <code>&lt;packaging&gt;sca-contribution-jar&lt;/packaging&gt;</code>. Any
 * required project dependencies (e.g. not scoped as provided) that are not themselves SCA contributions will be added to the archive's META-INF/lib
 * directory, making them available to the contribution and runtime extension classpaths. Dependencies and imports are defined as demonstrated in the
 * following example:
 * <pre>
 *    &lt;plugin&gt;
 *       &lt;groupId&gt;org.sca4j&lt;/groupId&gt;
 *       &lt;artifactId&gt;sca4j-contribution-plugin&lt;/artifactId&gt;
 *       &lt;version&gt;RELEASE&lt;/version&gt;
 *       &lt;extensions&gt;true&lt;/extensions&gt;
 *       &lt;configuration&gt;
 *          &lt;mavenImports&gt;
 *             &lt;mavenImport&gt;
 *                &lt;groupId&gt;org.mycompany.stuff&lt;/groupId&gt;
 *                &lt;artifactId&gt;myproject-something&lt;/artifactId&gt;
 *             &lt;/mavenImport&gt;
 *          &lt;/mavenImports&gt;
 *          &lt;deployables&gt;
 *             &lt;deployable targetNamespace=".." name="MyComposite"&gt;
 *          &lt;/deployables&gt;
 *       &lt;/configuration&gt;
 *     &lt;/plugin&gt;
 * </pre>
 * XML contributions are also supported using <code>&lt;packaging&gt;sca-contribution-xml&lt;/packaging&gt;</code>. Imports and exports are defined in
 * the same way as with archive contributions. However, the plugin inserts the contents of the composite.composite file (located in the project source
 * directory)  in the contribution output. The source composite file name may be ovverriden by specifying a value for <code>the compositeFile</code>
 * element in the plugin configuration.
 *
 * @version $Rev: 4138 $ $Date: 2008-05-05 19:15:27 +0100 (Mon, 05 May 2008) $
 * @goal package
 * @phase package
 */
public class SCA4JContributionMojo extends AbstractMojo {
    private static final String SCA_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String SCA4J_NS = "urn:sca4j.org";
    private static final String XML_PACKAGING = "sca-contribution-xml";
    private static final String JAR_PACKAGING = "sca-contribution-jar";

    private static final String[] DEFAULT_EXCLUDES = new String[]{"**/package.html"};
    private static final String[] DEFAULT_INCLUDES = new String[]{"**/**"};

    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory TF = TransformerFactory.newInstance();

    static {
        DBF.setNamespaceAware(true);
    }

    /**
     * Build output directory.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * Name of the generated composite archive.
     *
     * @parameter expression="${project.build.finalName}"
     */
    protected String contributionName;

    /**
     * Classifier to add to the generated artifact.
     *
     * @parameter
     */
    protected String classifier;

    /**
     * Directory containing the classes to include in the archive.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    protected File classesDirectory;

    /**
     * Standard Maven archive configuration.
     *
     * @parameter
     */
    protected MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * The Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     * @readonly
     */
    protected JarArchiver jarArchiver;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    protected MavenProjectHelper projectHelper;

    /**
     * @parameter expression="${project.packaging}
     * @required
     * @readonly
     */
    protected String packaging;

    /**
     * @parameter
     */
    protected String[] deployables;

    /**
     * @parameter
     */
    protected MavenImport[] mavenImports;

    /**
     * @parameter
     */
    protected File composite;

    /**
     * @parameter
     */
    protected String compositeFile = "composite.composite";

    public void execute() throws MojoExecutionException, MojoFailureException {
        File contribution;
        if (XML_PACKAGING.endsWith(packaging)) {
            // the project packaging is set to output an XML-based contribution
            contribution = createXmlContribution();
        } else {
            // the project packaging is set to output a ZIP-based contribution
            contribution = createArchive();
        }
        // set the contribution file for Maven
        if (classifier != null) {
            projectHelper.attachArtifact(project, "f3r", classifier, contribution);
        } else {
            project.getArtifact().setFile(contribution);
        }

    }

    /**
     * Outputs an XML contribution type.
     *
     * @return a File pointing to the contribution
     * @throws MojoExecutionException if an error occurs generating the contribution
     */
    private File createXmlContribution() throws MojoExecutionException {
        try {
            File file = new File(classesDirectory, compositeFile);
            if (!file.exists()) {
                throw new MojoExecutionException("Composite file not found: " + file);
            }
            // parse the composite input file
            Document compositeElement = DBF.newDocumentBuilder().parse(file);
            File contributionFile = new File(outputDirectory, "contribution.xml");
            Document document = DBF.newDocumentBuilder().newDocument();

            // create the root element for the contribution
            Element root = document.createElement("f3:xmlContribution");
            addNamespaces(root);

            document.appendChild(root);

            // create the contribution manifest
            Element contributionElement = document.createElement("contribution");
            generateSCAManifestContents(document, contributionElement);
            root.appendChild(contributionElement);

            // read and insert the composite as a child element
            Node copy = document.importNode(compositeElement.getDocumentElement(), true);
            root.appendChild(copy);

            // output the contribution file
            write(document, contributionFile);
            return contributionFile;
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Error building contribution", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error building contribution", e);
        } catch (ParserConfigurationException e) {
            throw new MojoExecutionException("Error building contribution", e);
        } catch (SAXException e) {
            throw new MojoExecutionException("Error building contribution", e);
        }
    }

    /**
     * Outputs a ZIP contribution.
     *
     * @return a File pointing to the created archive
     * @throws MojoExecutionException if an error occurs generating the contribution
     */
    private File createArchive() throws MojoExecutionException {

        File contribution = getJarFile(contributionName, classifier);

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(contribution);
        archive.setForced(true);

        try {
            if (!classesDirectory.exists()) {
                throw new FileNotFoundException(String.format("Unable to package contribution, %s does not exist.", classesDirectory));
            } else {
                includeDependencies();
                generateSCAManifest();
                archiver.getArchiver().addDirectory(classesDirectory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);
            }

            archiver.createArchive(project, archive);

            return contribution;
        }
        catch (Exception e) {
            throw new MojoExecutionException("Error assembling contribution", e);
        }

    }

    /**
     * Returns a File representing the name and location of the archive file to output.
     *
     * @param name       the archive name
     * @param classifier the classifier
     * @return a File representing the name and location of the archive file to output
     */
    private File getJarFile(String name, String classifier) {

        getLog().debug("Calculating the archive file name");
        if (classifier != null) {
            classifier = classifier.trim();
            if (classifier.length() > 0) {
                name = name + '-' + classifier;
            }
        }
        String extension = ".zip";
        if (JAR_PACKAGING.endsWith(packaging)) {
            extension = ".jar";
        }
        return new File(outputDirectory, name + extension);

    }

    /**
     * Adds required namespaces to the root contribution element.
     *
     * @param root the root element
     */
    private void addNamespaces(Element root) {
        root.setAttribute("xmlns", SCA_NS);
        root.setAttribute("xmlns:sca4j", SCA4J_NS);
    }


    /**
     * Generates an SCA manifest file.
     *
     * @throws MojoExecutionException if an error occurs generating the maniest
     */
    private void generateSCAManifest() throws MojoExecutionException {

        File srcContributionFile = new File(project.getBuild().getSourceDirectory(), "META-INF" + File.separator + "sca-contribution.xml");
        File contributionFile = new File(classesDirectory, "META-INF" + File.separator + "sca-contribution.xml");

        if ((deployables != null || mavenImports != null) && srcContributionFile.exists()) {
            throw new MojoExecutionException("SCA contribution xml already exists");
        }
        Document document;
        try {
            document = DBF.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new MojoExecutionException("Error generating contribution manifest", e);
        }

        Element root = document.createElement("contribution");
        addNamespaces(root);
        generateSCAManifestContents(document, root);
        document.appendChild(root);
        write(document, contributionFile);

    }

    /**
     * Generates the contents of the SCA manifest, adding them to the document root element.
     *
     * @param document            the document representing the manifest
     * @param contributionElement the contribution element
     * @throws MojoExecutionException if an error occurs generating the contents
     */
    private void generateSCAManifestContents(Document document, Element contributionElement) throws MojoExecutionException {
        generateDeployables(document, contributionElement);
        generateMavenImports(document, contributionElement);
    }

    /**
     * Generates imports for an SCA manifest.
     *
     * @param document            the manifest Document
     * @param contributionElement the contribution element
     */
    private void generateMavenImports(Document document, Element contributionElement) {

        if (mavenImports == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Set<Artifact> artifacts = (Set<Artifact>) project.getArtifacts();

        for (MavenImport mavenImport : mavenImports) {

            String groupId = mavenImport.getGroupId();
            String artifactId = mavenImport.getArtifactId();

            Element mavenImportElement = document.createElement("sca4j:import");
            mavenImportElement.setAttribute("groupId", groupId);
            mavenImportElement.setAttribute("artifactId", artifactId);

            for (Artifact artifact : artifacts) {
                if (groupId.equals(artifact.getGroupId()) && artifactId.equals(artifact.getArtifactId())) {
                    getLog().info("Found artifact:" + artifact.getArtifactId());
                    mavenImportElement.setAttribute("version", artifact.getVersion());
                }
            }

            contributionElement.appendChild(mavenImportElement);

        }

    }

    /**
     * Generates deployables for an SCA manifest.
     *
     * @param document            the manifest Document
     * @param contributionElement the contribution element
     * @throws MojoExecutionException if an error occurs generating the deployables
     */
    private void generateDeployables(Document document, Element contributionElement) throws MojoExecutionException {

        if (deployables == null) {
            return;
        }

        for (String deployable : deployables) {

            File deployableFile = new File(classesDirectory, deployable);
            Document composite;
            try {
                composite = DBF.newDocumentBuilder().parse(deployableFile);
            } catch (SAXException e) {
                throw new MojoExecutionException("Error generating deployables information", e);
            } catch (IOException e) {
                throw new MojoExecutionException("Error generating deployables information", e);
            } catch (ParserConfigurationException e) {
                throw new MojoExecutionException("Error generating deployables information", e);
            }
            Element compositeElement = composite.getDocumentElement();

            String uri = compositeElement.getAttribute("targetNamespace");
            String name = compositeElement.getAttribute("name");

            Element deployableElement = document.createElement("deployable");
            deployableElement.setAttribute("xmlns:dep", uri);
            deployableElement.setAttribute("composite", "dep:" + name);
            contributionElement.appendChild(deployableElement);

        }

    }

    /**
     * Copies all transitive dependencies to the output archive that are required for runtime operation, excluding other SCA contributions as they
     * will be deployed separately.
     *
     * @throws IOException if an error occurs copying the dependencies
     */
    private void includeDependencies() throws IOException {
        getLog().debug("Including dependencies in archive");
        File libDir = new File(classesDirectory, "META-INF" + File.separator + "lib");
        ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_RUNTIME);

        @SuppressWarnings("unchecked")
        Set<Artifact> artifacts = (Set<Artifact>) project.getArtifacts();
        for (Artifact artifact : artifacts) {
            getLog().debug("checking " + artifact.getArtifactId());
            boolean isSCAContribution = artifact.getType().startsWith("sca-contribution");
            if (!isSCAContribution && !artifact.isOptional() && filter.include(artifact)) {
                getLog().debug(String.format("including dependency %s", artifact));
                File destinationFile = new File(libDir, artifact.getFile().getName());
                if (!libDir.exists()) {
                    libDir.mkdirs();
                }
                getLog().debug(String.format("copying %s to %s", artifact.getFile(), destinationFile));
                FileChannel destChannel = new FileOutputStream(destinationFile).getChannel();
                FileChannel srcChannel = new FileInputStream(artifact.getFile()).getChannel();
                srcChannel.transferTo(0, srcChannel.size(), destChannel);
                destChannel.close();
                srcChannel.close();
            }
        }

    }

    /**
     * Outputs a Document to a file.
     *
     * @param document         the document to output
     * @param contributionFile the target file
     * @throws MojoExecutionException if an error writing to the file is encountered
     */
    private void write(Document document, File contributionFile) throws MojoExecutionException {
        try {
            Transformer transformer = TF.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            FileOutputStream out = new FileOutputStream(contributionFile);
            transformer.transform(new DOMSource(document), new StreamResult(out));
            out.close();
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Error writing contribution file", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing contribution file", e);
        } catch (TransformerException e) {
            throw new MojoExecutionException("Error writing contribution file", e);
        }
    }

}
