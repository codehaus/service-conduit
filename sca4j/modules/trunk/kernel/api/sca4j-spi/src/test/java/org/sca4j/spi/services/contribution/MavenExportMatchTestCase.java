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
package org.sca4j.spi.services.contribution;

import junit.framework.TestCase;

/**
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
public class MavenExportMatchTestCase extends TestCase {

    public void testDefaultVersionAndClassifier() {
        MavenExport export = new MavenExport();
        export.setGroupId("bar");
        export.setArtifactId("foo");
        MavenImport imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        assertEquals(Export.EXACT_MATCH, export.match(imprt));
    }

    public void testAnyVersionImport() {
        MavenExport export = new MavenExport();
        export.setGroupId("bar");
        export.setArtifactId("foo");
        export.setVersion("2.0");
        MavenImport imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        assertEquals(Export.EXACT_MATCH, export.match(imprt));
    }

    public void testNoMatchVersion() {
        MavenExport export = new MavenExport();
        export.setGroupId("bar");
        export.setArtifactId("foo");
        MavenImport imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("4.0");
        assertEquals(Export.NO_MATCH, export.match(imprt));
    }


    public void testVersion() {
        MavenExport export = new MavenExport();
        export.setGroupId("bar");
        export.setArtifactId("foo");
        export.setVersion("4.0.1");

        MavenImport imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("4.0");
        assertEquals(Export.EXACT_MATCH, export.match(imprt));

        imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("4.0.2");
        assertEquals(Export.NO_MATCH, export.match(imprt));

        imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("4.2.1");
        assertEquals(Export.NO_MATCH, export.match(imprt));

        imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("3.0");
        assertEquals(Export.EXACT_MATCH, export.match(imprt));

        imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("5.0");
        assertEquals(Export.NO_MATCH, export.match(imprt));

        imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("5.0.1");
        assertEquals(Export.NO_MATCH, export.match(imprt));

        imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("4.0.1-SNAPSHOT");
        assertEquals(Export.EXACT_MATCH, export.match(imprt));

    }

    public void testExportSnapshotVersion() {
        MavenExport export = new MavenExport();
        export.setGroupId("bar");
        export.setArtifactId("foo");
        export.setVersion("4.0.1-SNAPSHOT");
                         
        MavenImport imprt = new MavenImport();
        imprt.setGroupId("bar");
        imprt.setArtifactId("foo");
        imprt.setVersion("4.0.1");
        assertEquals(Export.NO_MATCH, export.match(imprt));
    }

}
