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
package org.sca4j.pojo.component;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

import org.sca4j.pojo.injection.ComponentObjectFactory;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.PropertyValue;
import org.sca4j.spi.AbstractLifecycle;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceFactory;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Base class for Component implementations based on Java objects.
 *
 * @version $Rev: 5283 $ $Date: 2008-08-26 17:37:53 +0100 (Tue, 26 Aug 2008) $
 * @param <T> the implementation class
 */
public abstract class PojoComponent<T> extends AbstractLifecycle implements AtomicComponent<T> {
    private final URI uri;
    private final InstanceFactoryProvider<T> provider;
    private final ScopeContainer<?> scopeContainer;
    private final URI groupId;
    private final int initLevel;
    private final long maxIdleTime;
    private final long maxAge;
    private InstanceFactory<T> instanceFactory;

    public PojoComponent(URI componentId,
                         InstanceFactoryProvider<T> provider,
                         ScopeContainer<?> scopeContainer,
                         URI groupId,
                         int initLevel,
                         long maxIdleTime,
                         long maxAge) {
        this.uri = componentId;
        this.provider = provider;
        this.scopeContainer = scopeContainer;
        this.groupId = groupId;
        this.initLevel = initLevel;
        this.maxIdleTime = maxIdleTime;
        this.maxAge = maxAge;
    }

    public URI getUri() {
        return uri;
    }

    public URI getGroupId() {
        return groupId;
    }

    public boolean isEagerInit() {
        return initLevel > 0;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void start() {
        super.start();
        scopeContainer.register(this);
    }

    public void stop() {
        instanceFactory = null;
        scopeContainer.unregister(this);
        super.stop();
    }

    public InstanceWrapper<T> createInstanceWrapper(WorkContext workContext) throws ObjectCreationException {
        return  getInstanceFactory().newInstance(workContext);
    }

    @SuppressWarnings({"unchecked"})
    public ObjectFactory<T> createObjectFactory() {
        return new ComponentObjectFactory(this, scopeContainer);
    }

    public <R> ObjectFactory<R> createObjectFactory(Class<R> type, String serviceName) throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public Map<String, PropertyValue> getDefaultPropertyValues() {
        return null;
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue> defaultPropertyValues) {
    }

    public ScopeContainer<?> getScopeContainer() {
        return scopeContainer;
    }

    public Class<T> getImplementationClass() {
        return provider.getImplementationClass();
    }

    /**
     * Sets an object factory.
     *
     * @param attribute     the InjectableAttribute identifying the component reference, property or context artifact the object factory creates
     *                      instances for
     * @param objectFactory the object factory
     */
    public void setObjectFactory(InjectableAttribute attribute, ObjectFactory<?> objectFactory) {
        setObjectFactory(attribute, objectFactory, null);
    }

    /**
     * Returns the object Factory by the given attribute
     * @param attribute
     * @return ObjectFactory
     */
    public ObjectFactory<?> getObjectFactory(InjectableAttribute attribute) {
 	 	      return provider.getObjectFactory(attribute);
 	}

    /**
     * Sets an object factory.
     *
     * @param attribute     the InjectableAttribute identifying the component reference, property or context artifact the object factory creates
     *                      instances for
     * @param objectFactory the object factory
     * @param key           key value for a Map reference
     */
    public void setObjectFactory(InjectableAttribute attribute, ObjectFactory<?> objectFactory, Object key) {
        scopeContainer.addObjectFactory(this, objectFactory, attribute.getName(), key);
        provider.setObjectFactory(attribute, objectFactory, key);
        // Clear the instance factory as it has changed and will need to be re-created. This can happen if reinjection occurs after the first 
        // instance has been created.
        instanceFactory = null;
    }

    public Class<?> getMemberType(InjectableAttribute injectionSite) {
        return provider.getMemberType(injectionSite);
    }

    public Type getGerenricMemberType(InjectableAttribute injectionSite) {
        return provider.getGenericType(injectionSite);
    }

    public String toString() {
        return "[" + uri.toString() + "] in state [" + super.toString() + ']';
    }

    private InstanceFactory<T> getInstanceFactory() {
        if (instanceFactory == null) {
            instanceFactory = provider.createFactory();
        }
        return instanceFactory;
    }

}
