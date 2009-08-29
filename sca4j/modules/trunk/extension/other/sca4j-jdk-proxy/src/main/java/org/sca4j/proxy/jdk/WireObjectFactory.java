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
package org.sca4j.proxy.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Creates a proxy for a wire that implements a provided interface
 *
 * @version $Rev: 2982 $ $Date: 2008-03-02 09:42:03 -0800 (Sun, 02 Mar 2008) $
 */
public class WireObjectFactory<T> implements ObjectFactory<T> {
    private Class<T> interfaze;
    private InteractionType type;
    private String callbackUri;
    private ProxyService proxyService;
    // the cache of proxy interface method to operation mappings
    private Map<Method, InvocationChain> mappings;

    /**
     * Constructor.
     *
     * @param interfaze    the interface to inject on the client
     * @param type         if the wire is stateless, conversational or propagates a conversational context
     * @param callbackUri  the callback URI for the wire or null if the wire is unidirectional
     * @param proxyService the wire service to create the proxy
     * @param mappings     proxy method to wire invocation chain mappings
     * @throws NoMethodForOperationException if a method matching the operation cannot be found
     */
    public WireObjectFactory(Class<T> interfaze,
                             InteractionType type,
                             String callbackUri,
                             ProxyService proxyService,
                             Map<Method, InvocationChain> mappings) throws NoMethodForOperationException {
        this.interfaze = interfaze;
        this.type = type;
        this.callbackUri = callbackUri;
        this.proxyService = proxyService;
        this.mappings = mappings;
    }


    public T getInstance() throws ObjectCreationException {
        return interfaze.cast(proxyService.createProxy(interfaze, type, callbackUri, mappings));
    }
}

