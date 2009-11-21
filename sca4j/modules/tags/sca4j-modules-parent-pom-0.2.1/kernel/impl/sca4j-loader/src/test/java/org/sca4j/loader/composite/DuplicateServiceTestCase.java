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
package org.sca4j.loader.composite;

import java.net.URI;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.osoa.sca.Constants.SCA_NS;

import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.scdl.ArtifactValidationFailure;
import org.sca4j.scdl.ComponentType;
import org.sca4j.scdl.CompositeService;
import org.sca4j.scdl.Implementation;

/**
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class DuplicateServiceTestCase extends TestCase {
    public static final String SERVICE_NAME = "notThere";

    private CompositeLoader loader;
    private XMLStreamReader reader;
    private IntrospectionContext ctx;

    /**
     * Verifies an exception is thrown if an attempt is made to configure a service twice.
     *
     * @throws Exception on test failure
     */
    public void testDuplicateService() throws Exception {
        loader.load(reader, ctx);
        ValidationFailure failure = ctx.getErrors().get(0);
        assertTrue(failure instanceof ArtifactValidationFailure);
        assertTrue(((ArtifactValidationFailure) failure).getFailures().get(0) instanceof DuplicateService);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TypeLoader<CompositeService> serviceLoader = createServiceLoader();
        LoaderRegistry registry = createRegistry();
        LoaderHelper helper = EasyMock.createNiceMock(LoaderHelper.class);
        EasyMock.replay(helper);
        loader = new CompositeLoader(registry, null, null, serviceLoader, null, null, null, helper);
        reader = createReader();
        ctx = new DefaultIntrospectionContext(URI.create("parent"), getClass().getClassLoader(), "foo");
    }

    private LoaderRegistry createRegistry() throws XMLStreamException, LoaderException {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        Implementation impl = createImpl();
        EasyMock.expect(registry.load(EasyMock.isA(XMLStreamReader.class),
                                      EasyMock.eq(Implementation.class),
                                      EasyMock.isA(IntrospectionContext.class))).andReturn(impl);

        EasyMock.replay(registry);
        return registry;
    }

    @SuppressWarnings({"unchecked"})
    private <T> TypeLoader<CompositeService> createServiceLoader()
            throws XMLStreamException, LoaderException {
        TypeLoader loader = EasyMock.createMock(TypeLoader.class);
        CompositeService value = new CompositeService(SERVICE_NAME, null, null);
        EasyMock.expect(loader.load(EasyMock.isA(XMLStreamReader.class),
                                    EasyMock.isA(IntrospectionContext.class))).andReturn(value).times(2);
        EasyMock.replay(loader);
        return (TypeLoader<CompositeService>) loader;
    }

    private Implementation createImpl() {
        Implementation<ComponentType> impl = new Implementation<ComponentType>() {
            public QName getType() {
                return null;
            }
        };
        ComponentType type = new ComponentType();
        CompositeService service = new CompositeService(SERVICE_NAME, null, null);

        type.add(service);
        impl.setComponentType(type);
        return impl;
    }

    private XMLStreamReader createReader() throws XMLStreamException {
        Location location = EasyMock.createNiceMock(Location.class);
        EasyMock.replay(location);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getNamespaceContext()).andStubReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("composite");
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn("http:///somenamepace");
        EasyMock.expect(reader.getAttributeValue(null, "local")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "constrainingType")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("true");

        EasyMock.expect(reader.nextTag()).andReturn(1);
        EasyMock.expect(reader.next()).andReturn(1);
        EasyMock.expect(reader.getName()).andReturn(new QName(SCA_NS, "service"));
        EasyMock.expect(reader.nextTag()).andReturn(1);
        EasyMock.expect(reader.next()).andReturn(1);
        EasyMock.expect(reader.getName()).andReturn(new QName(SCA_NS, "service"));
        EasyMock.expect(reader.getLocation()).andReturn(location);
        EasyMock.expect(reader.next()).andReturn(2);
        EasyMock.expect(reader.getName()).andReturn(new QName(SCA_NS, "composite"));
        EasyMock.replay(reader);
        return reader;
    }


}
