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
package org.sca4j.jaxb.runtime;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.jaxb.runtime.impl.JAXB2XmlTransformer;
import org.sca4j.jaxb.runtime.impl.TransformingInterceptor;
import org.sca4j.jaxb.runtime.impl.Xml2JAXBTransformer;

/**
 * @version $Revision$ $Date$
 */
public class TransformingInterceptorTestCase extends TestCase {
    private TransformingInterceptor<Object, String> interceptor;

    public void testInvokeServiceAndReturn() throws Exception {
        Interceptor mock = new MockEchoInterceptor();
        interceptor.setNext(mock);
        Foo foo = new Foo();
        Message msg = new MessageImpl();
        msg.setBody(new Object[]{foo});
        Message ret = interceptor.invoke(msg);
        assertTrue(ret.getBody() instanceof Foo);
    }

    public void testInvokeServiceOneWay() throws Exception {
        Interceptor mock = new MockOneWayInterceptor();
        interceptor.setNext(mock);
        Foo foo = new Foo();
        Message msg = new MessageImpl();
        msg.setBody(new Object[]{foo});
        Message ret = interceptor.invoke(msg);
        assertNull(ret.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
        JAXBContext context = JAXBContext.newInstance(Foo.class);
        JAXB2XmlTransformer inTransformer = new JAXB2XmlTransformer(context);
        Xml2JAXBTransformer outTransformer = new Xml2JAXBTransformer(context);
        interceptor = new TransformingInterceptor<Object, String>(inTransformer, outTransformer, getClass().getClassLoader());
    }

    private class MockEchoInterceptor implements Interceptor {
        public Message invoke(Message msg) {
            Object body = msg.getBody();
            Object payload = ((Object[]) body)[0];
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><foo/>", payload);
            // echo the results
            msg.setBody(payload);
            return msg;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }
    }

    private class MockOneWayInterceptor implements Interceptor {
        public Message invoke(Message msg) {
            Object body = msg.getBody();
            Object payload = ((Object[]) body)[0];
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><foo/>", payload);
            msg.setBody(null);
            return msg;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }
    }
}
