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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.fabric.services.contribution.processor;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.host.expression.ExpressionExpander;
import org.sca4j.host.expression.ExpressionExpansionException;
import org.sca4j.services.xmlfactory.impl.XMLFactoryImpl;
import org.sca4j.services.xmlfactory.XMLFactory;

/**
 * @version $Rev: 4226 $ $Date: 2008-05-15 10:38:18 +0100 (Thu, 15 May 2008) $
 */
public class XmlResourceProcessorTestCase extends TestCase {
    public static final QName QNAME = new QName("foo", "bar");
    public static final byte[] XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"foo\"/>".getBytes();
    public static final byte[] XML_DTD = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<!DOCTYPE definitions>" +
            "<definitions xmlns=\"foo\"/>").getBytes();

    private XmlResourceProcessor processor;
//    private LoaderRegistry registry;

    public void testDispatch() throws Exception {
//        InputStream stream = new ByteArrayInputStream(XML);
//        processor.process(stream);
//        EasyMock.verify(registry);
    }

    public void testDTDDispatch() throws Exception {
//        InputStream stream = new ByteArrayInputStream(XML_DTD);
//        processor.process(stream);
//        EasyMock.verify(registry);
    }

    @SuppressWarnings({"unchecked"})
    protected void setUp() throws Exception {
        super.setUp();
        XMLFactory factory = new XMLFactoryImpl(new ExpressionExpander() {
            public String expand(String value) throws ExpressionExpansionException {
                return value;
            }
        });
//        registry = EasyMock.createMock(LoaderRegistry.class);
//        EasyMock.expect(registry.load(EasyMock.isA(XMLStreamReader.class),
//                                      EasyMock.isA(Class.class),
//                                      EasyMock.isA(IntrospectionContext.class))).andReturn(null);
//        EasyMock.replay(registry);
        processor = new XmlResourceProcessor(null, null, null, factory);


    }
}
