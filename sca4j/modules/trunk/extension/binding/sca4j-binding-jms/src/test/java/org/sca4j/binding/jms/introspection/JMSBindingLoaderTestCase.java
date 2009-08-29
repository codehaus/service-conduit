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
package org.sca4j.binding.jms.introspection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.binding.jms.common.HeadersDefinition;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.scdl.JmsBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidPrefixException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.jaxb.control.api.JAXBTransformationService;
import org.sca4j.scdl.PolicyAware;
import org.w3c.dom.Document;

public class JMSBindingLoaderTestCase extends TestCase {
    public void testLoaderJMSBindingElement() throws Exception {
        LoaderHelper loaderHelper = new LoaderHelper() {

            public QName createQName(String name, XMLStreamReader reader) throws InvalidPrefixException {
                // TODO Auto-generated method stub
                return null;
            }

            public URI getURI(String target) {
                // TODO Auto-generated method stub
                return null;
            }

            public Document loadKey(XMLStreamReader reader) {
                // TODO Auto-generated method stub
                return null;
            }

            public void loadPolicySetsAndIntents(PolicyAware policyAware, XMLStreamReader reader, IntrospectionContext context) {
                // TODO Auto-generated method stub

            }

            public Document loadValue(XMLStreamReader reader) throws XMLStreamException {
                // TODO Auto-generated method stub
                return null;
            }

            public Set<QName> parseListOfQNames(XMLStreamReader reader, String attribute) throws InvalidPrefixException {
                // TODO Auto-generated method stub
                return null;
            }

            public List<URI> parseListOfUris(XMLStreamReader reader, String attribute) {
                // TODO Auto-generated method stub
                return null;
            }

        };
        JAXBTransformationService mock = new JAXBTransformationService() {

            public void registerBinding(QName name, QName dataType) {

            }
        };
        JmsBindingLoader loader = new JmsBindingLoader(loaderHelper, mock);
        InputStream inputStream = JmsBindingLoader.class.getResourceAsStream("JMSBindingLoaderTest.xml");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader streamReader = factory.createXMLStreamReader(new InputStreamReader(inputStream));
        JmsBindingDefinition jmsBinding = null;
        loaderHelper.loadKey(streamReader);
        while (streamReader.hasNext()) {
            if (START_ELEMENT == streamReader.next() && "binding.jms".equals(streamReader.getName().getLocalPart())) {
                jmsBinding = loader.load(streamReader, null);
                streamReader.close();
                break;
            }
        }
        JmsBindingMetadata metadata = jmsBinding.getMetadata();
        HeadersDefinition headers = metadata.getHeaders();
        assertEquals("CorrelationId", headers.getJMSCorrelationId());
        assertEquals("ThisType", headers.getJMSType());
        assertEquals("TestHeadersProperty", headers.getProperties().get("testHeadersProperty"));
        assertEquals(2, metadata.getOperationProperties().size());
        assertEquals("TestHeadersPropertyProperty", metadata.getOperationProperties().get("testOperationProperties1").getProperties().get("testHeadersPropertyProperty"));
        assertEquals("NestedHeader", metadata.getOperationProperties().get("testOperationProperties1").getHeaders().getProperties().get("nested"));
        assertEquals("NativeName", metadata.getOperationProperties().get("testOperationProperties2").getNativeOperation());
    }

}
