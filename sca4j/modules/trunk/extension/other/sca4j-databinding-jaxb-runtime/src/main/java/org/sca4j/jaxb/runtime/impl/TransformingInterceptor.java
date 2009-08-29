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
