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

package org.sca4j.contribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.sca4j.contribution.stub.SCAArtifactStub;
import org.sca4j.contribution.stub.SCAMavenProjectStub;
import org.sca4j.contribution.stub.SCAModelStub;

/* this file name must end with *TestCase.java, *Test.java does not get picked up as documented :(
 * This test is based off of the maven-war-plugin and maven-ejb-plugin test cases
 */
public class SCA4JContributionMojoTestCase extends AbstractMojoTestCase {

    protected File getTestDirectory(String testName) {
        return new File(getBasedir(), "target" + File.separator + "test-classes" + File.separator + "unit"
                + File.separator + testName);
    }

    /* configure the mojo for execution */
    protected SCA4JContributionMojo configureMojo(String testName, String type) throws Exception {
        File testDirectory = getTestDirectory(testName);
        File pomFile = new File(testDirectory, "test.xml");
        SCA4JContributionMojo mojo = (SCA4JContributionMojo) lookupMojo("package", pomFile);
        assertNotNull(mojo);
        File outputDir = new File(testDirectory, "target");
        setVariableValueToObject(mojo, "outputDirectory", outputDir);
        setVariableValueToObject(mojo, "classesDirectory", new File(outputDir, "classes"));
        setVariableValueToObject(mojo, "jarArchiver", new JarArchiver());
        Model model = new SCAModelStub();
        SCAMavenProjectStub stub = new SCAMavenProjectStub(model);
        stub.setFile(pomFile);
        SCAArtifactStub artifact = new SCAArtifactStub();
        artifact.setType(type);
        stub.setArtifact(artifact);
        setVariableValueToObject(mojo, "packaging", type);
        setVariableValueToObject(mojo, "contributionName", "test");
        setVariableValueToObject(mojo, "project", stub);
        return mojo;
    }

    public void testNoClassesDirectory() throws Exception {
        SCA4JContributionMojo mojo = configureMojo("no-directory", "sca-contribution");
        try {
            mojo.execute();
        } catch (Exception e) {
            assertTrue("exception not mojo exception", e instanceof MojoExecutionException);
            assertTrue(e.getCause() instanceof FileNotFoundException);
            assertTrue(e.getCause().getMessage().indexOf("does not exist") > -1);
            return;
        }
        fail("directory does not exist, should have failed");
    }

    public void testCorrect() throws Exception {
        SCA4JContributionMojo mojo = configureMojo("correct", "sca-contribution");
        try {
            mojo.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail("should have succeeded");
        }
        File testFile = new File(getTestDirectory("correct"), "target" + File.separator + "test.zip");
        assertTrue(testFile.exists());

        HashSet<String> jarContent = new HashSet<String>();
        JarFile jarFile = new JarFile(testFile);
        JarEntry entry;
        Enumeration<?> enumeration = jarFile.entries();

        while (enumeration.hasMoreElements()) {
            entry = (JarEntry) enumeration.nextElement();
            jarContent.add(entry.getName());
        }
        assertTrue("sca-contribution.xml file not found", jarContent.contains("META-INF/sca-contribution.xml"));
        assertTrue("content not found", jarContent.contains("test.properties"));

    }

    public void testDependencies() throws Exception {
        checkDependencies("sca-contribution");
    }

    public void testDependenciesJarType() throws Exception {
        checkDependencies("sca-contribution-jar");
    }

    private void checkDependencies(String type) throws Exception {
        SCA4JContributionMojo mojo = configureMojo("dependency", type);
        MavenProject p = mojo.project;
        Set artifacts = p.getArtifacts();
        SCAArtifactStub dep = new SCAArtifactStub();
        dep.setArtifactId("test-dep-1");
        dep.setFile(new File(getTestDirectory("dependency"), "dep-1.jar"));
        dep.setType("jar");
        artifacts.add(dep);
        dep = new SCAArtifactStub();
        dep.setArtifactId("test-dep-2");
        dep.setFile(new File(getTestDirectory("dependency"), "dep-2.jar"));
        dep.setType("sca-contribution");
        artifacts.add(dep);
        dep = new SCAArtifactStub();
        dep.setArtifactId("test-dep-3");
        dep.setFile(new File(getTestDirectory("dependency"), "dep-3.jar"));
        dep.setType("sca-contribution-jar");
        artifacts.add(dep);
        try {
            mojo.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail("should have succeeded");
        }
        File testFile = new File(getTestDirectory("dependency"), "target" + File.separator + mojo.contributionName
                + "." + getExtension(type));
        System.out.println("******" + testFile.getAbsolutePath());
        assertTrue(testFile.exists());

        HashSet jarContent = new HashSet();
        JarFile jarFile = new JarFile(testFile);
        JarEntry entry;
        Enumeration enumeration = jarFile.entries();

        while (enumeration.hasMoreElements()) {
            entry = (JarEntry) enumeration.nextElement();
            jarContent.add(entry.getName());
        }
        assertTrue("sca-contribution.xml file not found", jarContent.contains("META-INF/sca-contribution.xml"));
        assertTrue("content not found", jarContent.contains("test.properties"));
        assertTrue("dependency not added", jarContent.contains("META-INF/lib/dep-1.jar"));
        assertFalse("dependency of type sca-contribution should not have been added", jarContent
                .contains("META-INF/lib/dep-2.jar"));
        assertFalse("dependency of type sca-contribution-jar should not have been added", jarContent
                .contains("META-INF/lib/dep-3.jar"));
    }

    protected String getExtension(String packaging) {
        if ("sca-contribution-jar".equals(packaging)) {
            return "jar";
        }
        return "zip";

    }

}
