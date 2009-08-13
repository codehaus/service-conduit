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
package org.sca4j.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osoa.sca.ServiceUnavailableException;

import org.sca4j.spi.component.InstanceInvocationException;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Abstract callback handler implementation. Concrete classes must implement a strategy for mapping the callback target chain for the invoked callback
 * operation.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 10:40:37 -0700 (Mon, 14 May 2007) $
 */
public abstract class AbstractCallbackInvocationHandler<T> implements InvocationHandler {
    private final Class<T> interfaze;

    /**
     * Constructor.
     *
     * @param interfaze the callback service interface implemented by the proxy
     */
    public AbstractCallbackInvocationHandler(Class<T> interfaze) {
        // needed to implement ServiceReference
        this.interfaze = interfaze;
    }

    protected Object invoke(InvocationChain chain, Object[] args, WorkContext workContext) throws Throwable {
        // Pop the call frame as we move back in the request stack. When the invocation is made on the callback target, the same call frame state
        // will be present as existed when the initial forward request to this proxy's instance was dispatched to. Consequently,
        // CallFrame#getForwardCorrelaltionId() will return the correlation id for the callback target.
        CallFrame frame = workContext.popCallFrame();

        Interceptor headInterceptor = chain.getHeadInterceptor();
        assert headInterceptor != null;

        // send the invocation down the wire
        Message msg = new MessageImpl();
        msg.setBody(args);
        msg.setWorkContext(workContext);
        try {
            // dispatch the wire down the chain and get the response
            Message resp;
            try {
                resp = headInterceptor.invoke(msg);
            } catch (ServiceUnavailableException e) {
                // simply rethrow ServiceUnavailableExceptions
                throw e;
            } catch (RuntimeException e) {
                // wrap other exceptions raised by the runtime
                throw new ServiceUnavailableException(e);
            }

            // handle response from the application, returning or throwing is as appropriate
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw (Throwable) body;
            } else {
                return body;
            }
        } finally {
            // push the call frame for this component instance back onto the stack
            workContext.addCallFrame(frame);
        }
    }

    protected Object handleProxyMethod(Method method) throws InstanceInvocationException {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class)
                && "equals".equals(method.getName())) {
            // TODO implement
            throw new UnsupportedOperationException();
        } else if (Object.class.equals(method.getDeclaringClass())
                && "hashCode".equals(method.getName())) {
            return hashCode();
            // TODO beter hash algorithm
        }
        String op = method.getName();
        throw new InstanceInvocationException("Operation not configured: " + op, op);
    }

}
