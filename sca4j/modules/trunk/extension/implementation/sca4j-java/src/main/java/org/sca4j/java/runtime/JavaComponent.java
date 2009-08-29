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
package org.sca4j.java.runtime;

import java.net.URI;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

import org.sca4j.pojo.component.PojoComponent;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.services.proxy.ProxyService;

/**
 * The runtime instantiation of a Java component implementation.
 *
 * @version $Revision: 5243 $ $Date: 2008-08-20 22:14:37 +0100 (Wed, 20 Aug 2008) $
 * @param <T> the implementation class for the defined component
 */
public class JavaComponent<T> extends PojoComponent<T> {
    private final ProxyService proxyService;

    /**
     * Constructor for a Java Component.
     *
     * @param componentId             the component's uri
     * @param instanceFactoryProvider the provider for the instance factory
     * @param scopeContainer          the container for the component's implementation scope
     * @param groupId                 the component group this component belongs to
     * @param initLevel               the initialization level
     * @param maxIdleTime             the time after which idle instances of this component can be expired
     * @param maxAge                  the time after which instances of this component can be expired
     * @param proxyService            the service used to create reference proxies
     */
    public JavaComponent(URI componentId,
                         InstanceFactoryProvider<T> instanceFactoryProvider,
                         ScopeContainer<?> scopeContainer,
                         URI groupId,
                         int initLevel,
                         long maxIdleTime,
                         long maxAge,
                         ProxyService proxyService) {
        super(componentId, instanceFactoryProvider, scopeContainer, groupId, initLevel, maxIdleTime, maxAge);
        this.proxyService = proxyService;
    }

    public <B> B getService(Class<B> businessInterface, String referenceName) {
        throw new UnsupportedOperationException();
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public <B, R extends CallableReference<B>> R cast(B target) {
        return (R) proxyService.cast(target);
    }

}
