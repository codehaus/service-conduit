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
package org.sca4j.fabric.services.contribution;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.fabric.util.FileHelper;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.QNameExport;
import org.sca4j.spi.services.contribution.QNameImport;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;

/**
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public class MetaDataStoreImplTestCase extends TestCase {
    private static final URI RESOURCE_URI = URI.create("DefaultStore/test-resource");
    private static final URI RESOURCE_URI2 = URI.create("DefaultStore/test-resource2");
    private static final QName IMPORT_EXPORT_QNAME = new QName("test", "test");
    private static final QName IMPORT_EXPORT_QNAME2 = new QName("test2", "test2");
    private MetaDataStoreImpl store;

    public void testResolve() throws Exception {
        QNameImport imprt = new QNameImport(IMPORT_EXPORT_QNAME);
        Contribution contribution = store.resolve(imprt);
        assertEquals(RESOURCE_URI, contribution.getUri());
    }

    public void testTransitiveResolution() throws Exception {
        Contribution contribution = new Contribution(URI.create("resource"));
        ContributionManifest manifest = new ContributionManifest();
        QNameImport imprt = new QNameImport(IMPORT_EXPORT_QNAME2);
        manifest.addImport(imprt);
        contribution.setManifest(manifest);
        List<Contribution> contributions = store.resolveTransitiveImports(contribution);
        assertEquals(2, contributions.size());
    }

    public void testResolveContainingResource() throws Exception {
        URI uri = URI.create("resource");
        Contribution contribution = new Contribution(uri);
        ContributionManifest manifest = new ContributionManifest();
        contribution.setManifest(manifest);
        QName qname = new QName("foo", "bar");
        ResourceElement<QName, Serializable> element = new ResourceElement<QName, Serializable>(qname);
        Resource resource = new Resource(new URL("file://foo"));
        resource.addResourceElement(element);
        contribution.addResource(resource);
        store.store(contribution);
        assertEquals(resource, store.resolveContainingResource(uri, qname));
    }

    protected void setUp() throws Exception {
        super.setUp();
        store = new MetaDataStoreImpl();
        Contribution contribution = new Contribution(RESOURCE_URI);
        ContributionManifest manifest = new ContributionManifest();
        QNameExport export = new QNameExport(IMPORT_EXPORT_QNAME);
        manifest.addExport(export);
        contribution.setManifest(manifest);
        store.store(contribution);

        Contribution contribution2 = new Contribution(RESOURCE_URI2);
        ContributionManifest manifest2 = new ContributionManifest();
        QNameImport imprt = new QNameImport(IMPORT_EXPORT_QNAME);
        manifest2.addImport(imprt);
        QNameExport export2 = new QNameExport(IMPORT_EXPORT_QNAME2);
        manifest2.addExport(export2);
        contribution2.setManifest(manifest2);
        store.store(contribution2);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        FileHelper.deleteDirectory(new File("target/repository"));
    }

}
