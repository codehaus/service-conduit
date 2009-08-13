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
package org.sca4j.jaxb.runtime.impl;

import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationRuntimeException;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformationException;

/**
 * Transforms invocation parameters from one format to another.
 *
 * @version $Revision$ $Date$
 */
public class TransformingInterceptor<S, T> implements Interceptor {
    private Interceptor next;
    private final ClassLoader classLoader;
    private final PullTransformer<S, T> inTransformer;
    private final PullTransformer<T, S> outTransformer;

    public TransformingInterceptor(PullTransformer<S, T> inTransformer, PullTransformer<T, S> outTransformer, ClassLoader classLoader) {
        this.inTransformer = inTransformer;
        this.outTransformer = outTransformer;
        this.classLoader = classLoader;
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    @SuppressWarnings({"unchecked"})
    public Message invoke(Message message) {
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            Object payload = message.getBody();
            if (payload != null && payload.getClass().isArray()) {
                // the payload is an array if the invocation has preceeded from a Java proxy
                Object[] params = (Object[]) message.getBody();
                if (params.length > 0) {
                    Object[] transformed = new Object[params.length];
                    for (int i = 0; i < params.length; i++) {
                        Object param = params[i];
                        transformed[i] = inTransformer.transform((S) param, null);
                        message.setBody(transformed);
                    }
                }
            } else if (payload != null) {
                // the payload is a single value if it has been serialized from a transport
                // transform the response to the target format
                Object transformed = inTransformer.transform((S) payload, null);
                message.setBody(new Object[]{transformed});
            }
            Message response = next.invoke(message);
            T result = (T) response.getBody();
            if (result != null) {
                // transform the response to the incoming format
                Object transformed = outTransformer.transform(result, null);
                if (response.isFault()) {
                    response.setBodyWithFault(transformed);
                } else {
                    response.setBody(transformed);
                }
            }
            return response;
        } catch (TransformationException e) {
            throw new InvocationRuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

}
