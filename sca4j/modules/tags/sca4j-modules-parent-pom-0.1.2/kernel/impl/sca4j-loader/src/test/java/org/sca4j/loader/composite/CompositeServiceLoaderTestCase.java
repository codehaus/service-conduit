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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.loader.composite;

import java.net.URI;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.CompositeService;
import org.sca4j.scdl.ModelObject;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;

/**
 * Verifies loading of a service definition from an XML-based assembly
 *
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class CompositeServiceLoaderTestCase extends TestCase {
    private static final QName NAME = new QName("test", "binding");
    private final String serviceName = "service";
    private final String componentName = "component";
    private final String componentServiceName = "component/service";
    private final URI componentURI = URI.create("component");
    private final URI componentServiceURI = URI.create("component#service");

    private CompositeServiceLoader loader;
    private IntrospectionContext introspectionContext;
    private XMLStreamReader mockReader;
    private LoaderRegistry mockRegistry;
    private LoaderHelper mockLoaderHelper;

    public void testPromotedComponent() throws XMLStreamException {
        expect(mockReader.getAttributeCount()).andReturn(0);
        expect(mockReader.getAttributeValue(null, "name")).andReturn(serviceName);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(componentName);
        expect(mockLoaderHelper.getURI(componentName)).andReturn(componentURI);
        mockLoaderHelper.loadPolicySetsAndIntents(EasyMock.isA(CompositeService.class),
                                                  EasyMock.same(mockReader),
                                                  EasyMock.same(introspectionContext));
        expect(mockReader.next()).andReturn(END_ELEMENT);
        replay(mockReader, mockLoaderHelper);
        CompositeService serviceDefinition = loader.load(mockReader, introspectionContext);
        assertNotNull(serviceDefinition);
        assertEquals(serviceName, serviceDefinition.getName());
        assertEquals(componentURI, serviceDefinition.getPromote());
        verify(mockReader, mockLoaderHelper);
    }

    public void testPromotedService() throws XMLStreamException {
        expect(mockReader.getAttributeCount()).andReturn(0);
        expect(mockReader.getAttributeValue(null, "name")).andReturn(serviceName);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(componentServiceName);
        expect(mockLoaderHelper.getURI(componentServiceName)).andReturn(componentServiceURI);
        mockLoaderHelper.loadPolicySetsAndIntents(EasyMock.isA(CompositeService.class),
                                                  EasyMock.same(mockReader),
                                                  EasyMock.same(introspectionContext));
        expect(mockReader.next()).andReturn(END_ELEMENT);
        replay(mockReader, mockLoaderHelper);
        CompositeService serviceDefinition = loader.load(mockReader, introspectionContext);
        assertNotNull(serviceDefinition);
        assertEquals(serviceName, serviceDefinition.getName());
        assertEquals(componentServiceURI, serviceDefinition.getPromote());
        verify(mockReader, mockLoaderHelper);
    }

    public void testMultipleBindings() throws LoaderException, XMLStreamException {
        expect(mockReader.getAttributeCount()).andReturn(0);
        expect(mockReader.getAttributeValue(null, "name")).andReturn(serviceName);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(componentName);
        expect(mockLoaderHelper.getURI(componentName)).andReturn(componentURI);
        mockLoaderHelper.loadPolicySetsAndIntents(EasyMock.isA(CompositeService.class),
                                                  EasyMock.same(mockReader),
                                                  EasyMock.same(introspectionContext));

        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getEventType()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getEventType()).andReturn(END_ELEMENT);

        expect(mockReader.next()).andReturn(END_ELEMENT);

        BindingDefinition binding = new BindingDefinition(null, null, null) {
        };
        expect(mockRegistry.load(mockReader, ModelObject.class, introspectionContext)).andReturn(binding).times(2);
        replay(mockReader, mockLoaderHelper, mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(mockReader, introspectionContext);
        assertEquals(2, serviceDefinition.getBindings().size());
        verify(mockReader, mockLoaderHelper, mockRegistry);
    }

    public void testWithInterface() throws LoaderException, XMLStreamException {
        ServiceContract sc = new ServiceContract<Object>() {
            public boolean isAssignableFrom(ServiceContract<?> contract) {
                return false;
            }

            @Override
            public String getQualifiedInterfaceName() {
                return null;
            }

        };
        expect(mockReader.getAttributeCount()).andReturn(0);
        expect(mockReader.getAttributeValue(null, "name")).andReturn(serviceName);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(componentName);
        expect(mockLoaderHelper.getURI(componentName)).andReturn(componentURI);
        mockLoaderHelper.loadPolicySetsAndIntents(EasyMock.isA(CompositeService.class),
                                                  EasyMock.same(mockReader),
                                                  EasyMock.same(introspectionContext));

        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockRegistry.load(mockReader, ModelObject.class, introspectionContext)).andReturn(sc);
        expect(mockReader.getName()).andReturn(NAME);
        expect(mockReader.getEventType()).andReturn(END_ELEMENT);
        expect(mockReader.next()).andReturn(END_ELEMENT);

        replay(mockReader, mockLoaderHelper, mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(mockReader, introspectionContext);
        assertSame(sc, serviceDefinition.getServiceContract());
        verify(mockReader, mockLoaderHelper, mockRegistry);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockReader = EasyMock.createMock(XMLStreamReader.class);
        mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        mockLoaderHelper = EasyMock.createMock(LoaderHelper.class);
        loader = new CompositeServiceLoader(mockRegistry, mockLoaderHelper);
        introspectionContext = EasyMock.createMock(IntrospectionContext.class);
        EasyMock.replay(introspectionContext);
    }
}
