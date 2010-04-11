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
package org.sca4j.system.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationRuntimeException;

/**
 * @version $Rev: 4786 $ $Date: 2008-06-08 14:37:24 +0100 (Sun, 08 Jun 2008) $
 */
public class SystemInvokerInterceptor<T> implements Interceptor {

    private final Method operation;
    private final ScopeContainer<?> scopeContainer;
    private final AtomicComponent<T> component;

    public SystemInvokerInterceptor(Method operation, ScopeContainer<?> scopeContainer, AtomicComponent<T> component) {
        this.operation = operation;
        this.scopeContainer = scopeContainer;
        this.component = component;
    }

    public void setNext(Interceptor next) {
        throw new UnsupportedOperationException();
    }

    public Interceptor getNext() {
        return null;
    }

    public Message invoke(Message msg) {
        Object body = msg.getBody();
        WorkContext workContext = msg.getWorkContext();
        InstanceWrapper<T> wrapper;
        try {
            wrapper = scopeContainer.getWrapper(component, workContext);
        } catch (InstanceLifecycleException e) {
            throw new InvocationRuntimeException(e);
        }

        try {
            Object instance = wrapper.getInstance();
            WorkContext oldWorkContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
            try {
                msg.setBody(operation.invoke(instance, (Object[]) body));
            } catch (InvocationTargetException e) {
                msg.setBodyWithFault(e.getCause());
            } catch (IllegalAccessException e) {
                throw new InvocationRuntimeException(e);
            } finally {
                PojoWorkContextTunnel.setThreadWorkContext(oldWorkContext);
            }
            return msg;
        } finally {
            try {
                scopeContainer.returnWrapper(component, workContext, wrapper);
            } catch (InstanceDestructionException e) {
                throw new InvocationRuntimeException(e);
            }
        }
    }
}
