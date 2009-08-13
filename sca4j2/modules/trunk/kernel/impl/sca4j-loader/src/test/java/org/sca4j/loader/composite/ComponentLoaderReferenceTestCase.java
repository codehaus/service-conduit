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
package org.sca4j.loader.composite;

import java.net.URI;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.osoa.sca.Constants.SCA_NS;

import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.ComponentType;
import org.sca4j.scdl.Implementation;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * @version $Rev: 5128 $ $Date: 2008-08-01 17:15:16 +0100 (Fri, 01 Aug 2008) $
 */
public class ComponentLoaderReferenceTestCase extends TestCase {
    public static final String REF_NAME = "notThere";

    private ComponentLoader loader;
    private XMLStreamReader reader;
    private IntrospectionContext ctx;

    /**
     * Verifies an exception is thrown if an attempt is made to configure a non-existent reference.
     *
     * @throws Exception on test failure
     */
    public void testNoReference() throws Exception {
        loader.load(reader, ctx);
        assertTrue(ctx.getErrors().get(0) instanceof ComponentReferenceNotFound);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TypeLoader<ComponentReference> referenceLoader = createReferenceLoader();
        Loader registry = createRegistry();
        LoaderHelper helper = EasyMock.createNiceMock(LoaderHelper.class);
        EasyMock.replay(helper);
        loader = new ComponentLoader(registry, null, referenceLoader, null, helper);
        reader = createReader();
        ctx = new DefaultIntrospectionContext(URI.create("parent"), getClass().getClassLoader(), "foo");
    }

    private Loader createRegistry() throws XMLStreamException, LoaderException {
        Loader registry = EasyMock.createMock(Loader.class);
        Implementation impl = createImpl();
        EasyMock.expect(registry.load(EasyMock.isA(XMLStreamReader.class),
                                      EasyMock.eq(Implementation.class),
                                      EasyMock.isA(IntrospectionContext.class))).andReturn(impl);

        EasyMock.replay(registry);
        return registry;
    }

    @SuppressWarnings({"unchecked"})
    private TypeLoader<ComponentReference> createReferenceLoader() throws XMLStreamException, LoaderException {
        TypeLoader<ComponentReference> referenceLoader = EasyMock.createMock(TypeLoader.class);
        ComponentReference reference = new ComponentReference(REF_NAME);
        EasyMock.expect(referenceLoader.load(EasyMock.isA(XMLStreamReader.class),
                                             EasyMock.isA(IntrospectionContext.class))).andReturn(reference);
        EasyMock.replay(referenceLoader);
        return referenceLoader;
    }

    private Implementation createImpl() {
        Implementation<ComponentType> impl = new Implementation<ComponentType>() {
            public QName getType() {
                return null;
            }
        };
        impl.setComponentType(new ComponentType());
        return impl;
    }

    private XMLStreamReader createReader() throws XMLStreamException {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        Location location = EasyMock.createNiceMock(Location.class);
        EasyMock.replay(location);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("component");
        EasyMock.expect(reader.getName()).andReturn(new QName("implementation.test")).times(2);
        EasyMock.expect(reader.getEventType()).andReturn(2);
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "runtimeId")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "initLevel")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(EasyMock.isA(String.class), EasyMock.eq("key"))).andReturn(null);
        EasyMock.expect(reader.nextTag()).andReturn(1);
        EasyMock.expect(reader.next()).andReturn(1);
        EasyMock.expect(reader.getName()).andReturn(new QName(SCA_NS, "reference"));
        EasyMock.expect(reader.getLocation()).andReturn(location);
        EasyMock.expect(reader.next()).andReturn(2);
        EasyMock.expect(reader.getName()).andReturn(new QName(SCA_NS, "component"));
        EasyMock.replay(reader);
        return reader;
    }


}
