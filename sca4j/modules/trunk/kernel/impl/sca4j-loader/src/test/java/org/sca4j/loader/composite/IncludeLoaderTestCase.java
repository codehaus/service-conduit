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
import java.net.URL;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.Include;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.ResourceElement;

/**
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class IncludeLoaderTestCase extends TestCase {

    private LoaderRegistry registry;
    private IncludeLoader loader;
    private XMLStreamReader reader;
    private NamespaceContext namespaceContext;
    private IntrospectionContext context;
    private URL base;
    private URL includeURL;
    private ClassLoader cl;
    private String namespace;
    private QName name;
    private MetaDataStore store;
    private Composite composite;

    public void testResolveQName() throws Exception {
        expect(reader.getAttributeCount()).andReturn(0);
        expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        expect(reader.getNamespaceContext()).andReturn(namespaceContext);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.getAttributeValue(null, "scdlResource")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);

        expect(context.getTargetNamespace()).andReturn(namespace);
        expect(context.getTargetClassLoader()).andReturn(cl);
        expect(context.getContributionUri()).andReturn(null);
        QNameSymbol symbol = new QNameSymbol(name);
        Composite include = new Composite(name);
        ResourceElement<QNameSymbol, Composite> element = new ResourceElement<QNameSymbol, Composite>(symbol);
        element.setValue(include);
        // FIXME null check
        expect(store.resolve((URI) EasyMock.isNull(), eq(Composite.class), isA(QNameSymbol.class), isA(ValidationContext.class))).andReturn(element);
        replay(registry, reader, namespaceContext, context, store);

        loader.load(reader, context);
        verify(registry, reader, namespaceContext, context, store);
    }

    public void testWithAbsoluteScdlLocation() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeCount()).andReturn(0);
        expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        expect(reader.getNamespaceContext()).andReturn(namespaceContext);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn("file:/include.scdl");
        expect(reader.getAttributeValue(null, "scdlResource")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);

        expect(context.getSourceBase()).andReturn(base);
        expect(context.getTargetNamespace()).andReturn(namespace);
        expect(context.getTargetClassLoader()).andReturn(cl);
        expect(context.getContributionUri()).andReturn(null);

        expect(registry.load(
                eq(includeURL),
                eq(Composite.class),
                isA(IntrospectionContext.class)))
                .andReturn(composite);
        replay(registry, reader, namespaceContext, context, store);

        Include include = loader.load(reader, context);
        assertEquals(name, include.getName());
        assertEquals(includeURL, include.getScdlLocation());
        verify(registry, reader, namespaceContext, context);
    }

    public void testWithRelativeScdlLocation() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeCount()).andReturn(0);
        expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        expect(reader.getNamespaceContext()).andReturn(namespaceContext);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn("include.scdl");
        expect(reader.getAttributeValue(null, "scdlResource")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);

        expect(context.getSourceBase()).andReturn(base);
        expect(context.getTargetNamespace()).andReturn(namespace);
        expect(context.getTargetClassLoader()).andReturn(cl);
        expect(context.getContributionUri()).andReturn(null);

        expect(registry.load(
                eq(includeURL),
                eq(Composite.class),
                isA(IntrospectionContext.class)))
                .andReturn(composite);
        replay(registry, reader, namespaceContext, context, store);

        Include include = loader.load(reader, context);
        assertEquals(name, include.getName());
        assertEquals(includeURL, include.getScdlLocation());
        verify(registry, reader, namespaceContext, context);
    }

    public void testWithScdlResource() throws LoaderException, XMLStreamException {
        String resource = "org/sca4j/loader/composite/test-include.composite";
        includeURL = cl.getResource(resource);
        assertNotNull(includeURL);

        expect(reader.getAttributeCount()).andReturn(0);
        expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        expect(reader.getNamespaceContext()).andReturn(namespaceContext);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.getAttributeValue(null, "scdlResource")).andReturn(resource);
        expect(reader.next()).andReturn(END_ELEMENT);

        expect(context.getTargetNamespace()).andReturn(namespace);
        expect(context.getTargetClassLoader()).andReturn(cl);
        expect(context.getContributionUri()).andReturn(null);

        expect(registry.load(
                eq(includeURL),
                eq(Composite.class),
                isA(IntrospectionContext.class)))
                .andReturn(composite);
        replay(registry, reader, namespaceContext, context, store);

        Include include = loader.load(reader, context);
        assertEquals(name, include.getName());
        assertEquals(includeURL, include.getScdlLocation());
        verify(registry, reader, namespaceContext, context);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = createMock(LoaderRegistry.class);
        reader = createMock(XMLStreamReader.class);
        namespaceContext = createMock(NamespaceContext.class);
        namespace = "urn:example.com:xmlns";
        context = createMock(IntrospectionContext.class);
        cl = getClass().getClassLoader();
        base = new URL("file:/test.scdl");
        includeURL = new URL("file:/include.scdl");
        store = createMock(MetaDataStore.class);
        loader = new IncludeLoader(registry, store);
        name = new QName(namespace, "foo");
        composite = new Composite(null);
    }
}
