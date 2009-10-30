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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.osoa.sca.Constants.SCA_NS;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.PolicyAware;

/**
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class CompositeLoaderTestCase extends TestCase {
    public static final QName COMPOSITE = new QName(SCA_NS, "composite");
    private CompositeLoader loader;
    private QName name;
    private IntrospectionContext introspectionContext;

    public void testLoadNameAndDefaultAutowire() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getNamespaceContext()).andStubReturn(null);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "local")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "constrainingType")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader, introspectionContext);
        Composite type = loader.load(reader, introspectionContext);
        assertEquals(name, type.getName());
        assertEquals(Autowire.INHERITED, type.getAutowire());
        EasyMock.verify(reader, introspectionContext);
    }

    public void testAutowire() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getNamespaceContext()).andStubReturn(null);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "local")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "constrainingType")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("true");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader, introspectionContext);
        Composite type = loader.load(reader, introspectionContext);
        assertEquals(Autowire.ON, type.getAutowire());
        EasyMock.verify(reader, introspectionContext);
    }

    protected void setUp() throws Exception {
        super.setUp();
        introspectionContext = EasyMock.createMock(IntrospectionContext.class);
        EasyMock.expect(introspectionContext.getSourceBase()).andStubReturn(null);
        EasyMock.expect(introspectionContext.getTargetClassLoader()).andStubReturn(null);
        EasyMock.expect(introspectionContext.getContributionUri()).andStubReturn(null);
        EasyMock.expect(introspectionContext.getTypeMapping()).andStubReturn(null);

        LoaderHelper loaderHelper = EasyMock.createMock(LoaderHelper.class);
        loaderHelper.loadPolicySetsAndIntents(EasyMock.isA(PolicyAware.class),
                                              EasyMock.isA(XMLStreamReader.class),
                                              EasyMock.isA(IntrospectionContext.class));
        EasyMock.replay(loaderHelper);

        loader = new CompositeLoader(null, null, null, null, null, null, null, loaderHelper);
        name = new QName("http://example.com", "composite");
    }
}
