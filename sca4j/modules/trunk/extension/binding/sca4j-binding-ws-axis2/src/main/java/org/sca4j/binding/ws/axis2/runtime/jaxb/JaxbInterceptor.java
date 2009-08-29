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
package org.sca4j.binding.ws.axis2.runtime.jaxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebFault;

import org.apache.axiom.om.OMElement;

import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.invocation.Message;

/**
 * Interceptor that transforms an OMElement to a JAXB bound object on the way in and JAXB bound object to OMElement on the way out.
 * <p/>
 * TODO The interceptor assumes doc-lit wrapped, this may need to be fixed.
 *
 * @version $Revision$ $Date$
 */
public class JaxbInterceptor implements Interceptor {

    private Interceptor next;
    private final ClassLoader classLoader;
    private final OMElement2Jaxb inTransformer;
    private final Jaxb2OMElement outTransformer;
    private final boolean service;
    private final Map<Class<?>, Constructor<?>> faultMapping;

    public JaxbInterceptor(ClassLoader classLoader, JAXBContext jaxbContext, boolean service, Map<Class<?>, Constructor<?>> faultMapping) throws JAXBException {
        this.classLoader = classLoader;
        inTransformer = new OMElement2Jaxb(jaxbContext);
        outTransformer = new Jaxb2OMElement(jaxbContext);
        this.service = service;
        this.faultMapping = faultMapping;
    }

    public Interceptor getNext() {
        return next;
    }

    public Message invoke(Message message) {

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            return service ? interceptService(message) : interceptReference(message);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

    }

    private Message interceptService(Message message) {

        Object[] payload = (Object[]) message.getBody();

        if (payload != null && payload.length > 0) {
            OMElement omElement = (OMElement) payload[0];
            Object jaxbObject = inTransformer.transform(omElement, null);
            message.setBody(new Object[]{jaxbObject});
        }

        Message response = next.invoke(message);
        Object result;
        if (response.isFault()) {
            Object webFault = response.getBody();
            result = getFault(webFault);
        } else {
            result = response.getBody();
        }

        if (result != null) {
            OMElement omElement = outTransformer.transform(result, null);
            response.setBody(omElement);
        }

        return response;

    }

    private Object getFault(Object webFault) {
        
        WebFault annotation = webFault.getClass().getAnnotation(WebFault.class);
        if (annotation == null) {
            // this is an undeclared exception
            if (webFault instanceof RuntimeException) {
                throw (RuntimeException) webFault;
            } else if (webFault instanceof Exception) {
                throw new AssertionError((Exception) webFault);
            } else if (webFault instanceof Error) {
                throw (Error) webFault;
            }
        }
        
        try {
            Method getFaultInfo = webFault.getClass().getMethod("getFaultInfo");
            return getFaultInfo.invoke(webFault);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

    }

    private Message interceptReference(Message message) {

        Object[] payload = (Object[]) message.getBody();

        if (payload != null && payload.length > 0) {
            Object jaxbObject = payload[0];
            OMElement omElement = outTransformer.transform(jaxbObject, null);
            message.setBody(new Object[]{omElement});
        }

        Message response = next.invoke(message);
        Object result = response.getBody();

        if (result != null) {
            OMElement omElement = (OMElement) result;
            Object jaxbObject = inTransformer.transform(omElement, null);
            Constructor<?> faultConstructor = faultMapping.get(jaxbObject.getClass());
            if (faultConstructor != null) {
                // the received message maps to a fault
                try {
                    Object fault = faultConstructor.newInstance(null, jaxbObject);
                    response.setBodyWithFault(fault);
                } catch (InstantiationException e) {
                    throw new AssertionError();
                } catch (IllegalAccessException e) {
                    throw new AssertionError();
                } catch (InvocationTargetException e) {
                    throw new AssertionError();
                }
            } else {
                // the received message does not map to a fault so assume it's a normal response
                response.setBody(jaxbObject);
            }
        }

        return response;

    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

}
