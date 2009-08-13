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

package org.sca4j.fabric.services.contribution;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.fabric.util.FileHelper;
import org.sca4j.fabric.services.classloading.ClassLoaderRegistryImpl;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.QNameExport;
import org.sca4j.spi.services.contribution.QNameImport;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

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
        QNameSymbol symbol = new QNameSymbol(qname);
        ResourceElement<QNameSymbol, Serializable> element = new ResourceElement<QNameSymbol, Serializable>(symbol);
        Resource resource = new Resource(new URL("file://foo"), "resource");
        resource.addResourceElement(element);
        contribution.addResource(resource);
        store.store(contribution);
        assertEquals(resource, store.resolveContainingResource(uri, symbol));
    }

    protected void setUp() throws Exception {
        super.setUp();
        ClassLoaderRegistry registry = new ClassLoaderRegistryImpl();
        registry.register(URI.create("resource"), getClass().getClassLoader());
        store = new MetaDataStoreImpl(registry, null);
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
