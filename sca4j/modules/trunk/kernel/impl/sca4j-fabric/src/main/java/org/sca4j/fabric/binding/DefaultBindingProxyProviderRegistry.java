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
 */
package org.sca4j.fabric.binding;

import java.net.URI;
import java.util.Map;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Reference;
import org.sca4j.spi.binding.BindingProxyProvider;
import org.sca4j.spi.binding.BindingProxyProviderRegistry;

/**
 * @author meerajk
 *
 */
public class DefaultBindingProxyProviderRegistry implements BindingProxyProviderRegistry {
    
    @Reference(required = false) protected Map<QName, BindingProxyProvider> bindingProxyProviders;

    /**
     * @see org.sca4j.spi.binding.BindingProxyProviderRegistry#getBinding(javax.xml.namespace.QName, java.lang.Class, java.net.URI, javax.xml.namespace.QName[])
     */
    public <T> T getBinding(QName bindingType, Class<T> endpointInterface, URI endpoint, QName... intents) {
        return bindingProxyProviders.get(bindingType).getBinding(endpointInterface, endpoint, intents);
    }

}
