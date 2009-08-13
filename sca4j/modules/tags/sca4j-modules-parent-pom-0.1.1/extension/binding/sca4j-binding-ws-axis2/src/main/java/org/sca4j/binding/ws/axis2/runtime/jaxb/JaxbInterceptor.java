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
