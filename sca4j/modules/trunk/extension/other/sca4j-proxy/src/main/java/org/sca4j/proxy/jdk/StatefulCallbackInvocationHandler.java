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
package org.sca4j.proxy.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Responsible for dispatching to a callback service from a component implementation instance that is not composite scope. Since only one client can
 * invoke the instance this proxy is injected on at a time, there can only be one callback target, even if the proxy is injected on an instance
 * variable. Consequently, the proxy does not need to map the callback target based on the forward request.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 10:40:37 -0700 (Mon, 14 May 2007) $
 */
public class StatefulCallbackInvocationHandler<T> extends AbstractCallbackInvocationHandler<T> {
    private Map<Method, InvocationChain> chains;
    private ScopeContainer scopeContainer;

    /**
     * Constructor.
     *
     * @param interfaze the callback service interface implemented by the proxy
     * @param chains    the invocation chain mappings for the callback wire
     */
    public StatefulCallbackInvocationHandler(Class<T> interfaze, Map<Method, InvocationChain> chains) {
        super(interfaze);
        this.chains = chains;
    }

    /**
     * Constructor.
     *
     * @param interfaze      the callback service interface implemented by the proxy
     * @param scopeContainer the conversational scope container
     * @param chains         the invocation chain mappings for the callback wire
     */
    public StatefulCallbackInvocationHandler(Class<T> interfaze, ScopeContainer<?> scopeContainer, Map<Method, InvocationChain> chains) {
        super(interfaze);
        this.scopeContainer = scopeContainer;
        this.chains = chains;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        // find the invocation chain for the invoked operation
        InvocationChain chain = chains.get(method);
        if (chain == null) {
            return handleProxyMethod(method);
        }
        try {
            return super.invoke(chain, args, workContext);
        } finally {
            if (chain.getPhysicalOperation().isEndsConversation()) {
                scopeContainer.stopContext(workContext);
            }
        }
    }

}
