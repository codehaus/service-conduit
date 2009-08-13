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
package org.sca4j.fabric.services.contribution.manifest;

import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.MavenExport;

/**
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
public class MavenPOMProcessorTestCase extends TestCase {
    public static final String NS = "http://maven.apache.org/POM/4.0.0";
    public static final String POM =
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                    "    <modelVersion>4.0.0</modelVersion>\n" +
                    "    <parent>\n" +
                    "        <groupId>org.sca4j</groupId>\n" +
                    "        <artifactId>sca4j-kernel-impl-parent-pom</artifactId>\n" +
                    "        <version>0.5BETA2-SNAPSHOT</version>\n" +
                    "    </parent>\n" +
                    "    <groupId>foo</groupId>\n" +
                    "    <artifactId>bar</artifactId>\n" +
                    "    <name>Test Pom</name>\n" +
                    "    <package>jar</package>" +
                    "    <version>1.0-SNAPSHOT</version>\n" +
                    "    <description>test pom</description>\n" +
                    "\n" +
                    "    <dependencies>\n" +
                    "\n" +
                    "        <dependency>\n" +
                    "            <groupId>org.sca4j</groupId>\n" +
                    "            <artifactId>sca4j-spi</artifactId>\n" +
                    "            <version>${project.version}</version>\n" +
                    "        </dependency>\n" +
                    "\t\t\n" +
                    "    </dependencies>\n" +
                    "\n" +
                    "\n" +
                    "</project>";

    private MavenPOMProcessor processor = new MavenPOMProcessor(null);
    private XMLStreamReader reader;

    public void testParse() throws Exception {
        ContributionManifest manifest = new ContributionManifest();
        ValidationContext context = new DefaultValidationContext();
        processor.process(manifest, reader, context);
        MavenExport export = (MavenExport) manifest.getExports().get(0);
        assertEquals("foo", export.getGroupId());
        assertEquals("bar", export.getArtifactId());
        assertEquals("1.0-SNAPSHOT", export.getVersion());
        assertEquals("jar", export.getClassifier());
    }


    protected void setUp() throws Exception {
        super.setUp();
        ByteArrayInputStream b = new ByteArrayInputStream(POM.getBytes());
        reader = XMLInputFactory.newInstance().createXMLStreamReader(b);
        reader.nextTag();
    }


}
