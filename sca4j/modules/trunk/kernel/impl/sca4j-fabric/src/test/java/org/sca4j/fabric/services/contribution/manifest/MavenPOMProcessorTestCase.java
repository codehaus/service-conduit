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
