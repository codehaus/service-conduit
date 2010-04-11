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
package org.sca4j.fabric.implementation.singleton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.ComponentContext;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.PropertyValue;
import org.sca4j.spi.AbstractLifecycle;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.SingletonObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceInitializationException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Wraps an object intended to service as a system component provided to the SCA4J runtime by the host environment.
 *
 * @version $$Rev: 5258 $$ $$Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $$
 */
public class SingletonComponent<T> extends AbstractLifecycle implements AtomicComponent<T> {
    private final URI uri;
    private T instance;
    private Map<Member, InjectableAttribute> sites;
    private InstanceWrapper<T> wrapper;
    private Map<String, PropertyValue> defaultPropertyValues;
    private Map<ObjectFactory, InjectableAttribute> reinjectionValues;

    public SingletonComponent(URI componentId, T instance, Map<InjectionSite, InjectableAttribute> mappings) {
        this.uri = componentId;
        this.instance = instance;
        this.wrapper = new SingletonWrapper<T>(instance);
        this.reinjectionValues = new HashMap<ObjectFactory, InjectableAttribute>();
        initializeInjectionSites(instance, mappings);
    }

    public String getKey() {
        return null;
    }

    public URI getUri() {
        return uri;
    }

    public URI getGroupId() {
        return null;
    }

    public boolean isEagerInit() {
        return false;
    }

    public int getInitLevel() {
        return 0;
    }

    public long getMaxIdleTime() {
        return -1;
    }

    public long getMaxAge() {
        return -1;
    }

    public InstanceWrapper<T> createInstanceWrapper(WorkContext workContext) throws ObjectCreationException {
        return wrapper;
    }

    public ObjectFactory<T> createObjectFactory() {
        return new SingletonObjectFactory<T>(instance);
    }

    public <R> ObjectFactory<R> createObjectFactory(Class<R> type, String serviceName) throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public ComponentContext getComponentContext() {
        // singleton components do not provide a component context
        return null;
    }

    public Map<String, PropertyValue> getDefaultPropertyValues() {
        return defaultPropertyValues;
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue> defaultPropertyValues) {
        this.defaultPropertyValues = defaultPropertyValues;
    }

    /**
     * Adds an ObjectFactory to be reinjected
     *
     * @param attribute    the InjectableAttribute describing the site to reinject
     * @param paramFactory the object factory responsible for supplying a value to reinject
     */
    public void addObjectFactory(InjectableAttribute attribute, ObjectFactory paramFactory) {
        reinjectionValues.put(paramFactory, attribute);
    }

    public String toString() {
        return "[" + uri.toString() + "] in state [" + super.toString() + ']';
    }

    /**
     * Obtain the fields and methods for injection sites associated with the instance
     *
     * @param instance the instance this component wraps
     * @param mappings the mappings of injection sites
     */
    private void initializeInjectionSites(T instance, Map<InjectionSite, InjectableAttribute> mappings) {
        this.sites = new HashMap<Member, InjectableAttribute>();
        for (Map.Entry<InjectionSite, InjectableAttribute> entry : mappings.entrySet()) {
            InjectionSite site = entry.getKey();
            if (site instanceof FieldInjectionSite) {
                try {
                    Field field = getField(((FieldInjectionSite) site).getName());
                    sites.put(field, entry.getValue());
                } catch (NoSuchFieldException e) {
                    // programming error
                    throw new AssertionError(e);
                }
            } else if (site instanceof MethodInjectionSite) {
                MethodInjectionSite methodInjectionSite = (MethodInjectionSite) site;
                try {
                    Method method = methodInjectionSite.getSignature().getMethod(instance.getClass());
                    sites.put(method, entry.getValue());
                } catch (ClassNotFoundException e) {
                    // programming error
                    throw new AssertionError(e);
                } catch (NoSuchMethodException e) {
                    // programming error
                    throw new AssertionError(e);
                }

            } else {
                // ignore other injection sites
            }
        }
    }

    private Field getField(String name) throws NoSuchFieldException {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private class SingletonWrapper<T> implements InstanceWrapper<T> {

        private final T instance;

        private SingletonWrapper(T instance) {
            this.instance = instance;
        }

        public T getInstance() {
            return instance;
        }

        public boolean isStarted() {
            return true;
        }

        public void start(WorkContext workcontext) throws InstanceInitializationException {
        }

        public void stop(WorkContext workContext) throws InstanceDestructionException {
        }

        public void reinject() {
            for (Map.Entry<ObjectFactory, InjectableAttribute> entry : reinjectionValues.entrySet()) {
                try {
                    inject(entry.getValue(), entry.getKey());
                } catch (ObjectCreationException e) {
                    throw new AssertionError(e);
                }
            }
            reinjectionValues.clear();
        }

        /**
         * Injects a new value on a field or method of the instance.
         *
         * @param attribute the InjectableAttribute defining the field or method
         * @param factory   the ObjectFactory that returns the value to inject
         * @throws ObjectCreationException if an error occurs during injection
         */
        private void inject(InjectableAttribute attribute, ObjectFactory factory) throws ObjectCreationException {
            for (Map.Entry<Member, InjectableAttribute> entry : sites.entrySet()) {
                if (entry.getValue().equals(attribute)) {
                    Member member = entry.getKey();
                    if (member instanceof Field) {
                        try {
                            Object param = factory.getInstance();
                            ((Field) member).set(instance, param);
                        } catch (IllegalAccessException e) {
                            // should not happen as accessibility is already set
                            throw new ObjectCreationException(e);
                        }
                    } else if (member instanceof Method) {
                        try {
                            Object param = factory.getInstance();
                            ((Method) member).invoke(instance, param);
                        } catch (IllegalAccessException e) {
                            // should not happen as accessibility is already set
                            throw new ObjectCreationException(e);
                        } catch (InvocationTargetException e) {
                            throw new ObjectCreationException(e);
                        }
                    } else {
                        // programming error
                        throw new ObjectCreationException("Unsupported member type" + member);
                    }
                }
            }
        }

        public void addObjectFactory(String referenceName, ObjectFactory<?> factory, Object key) {
            // no-op
        }

    }
}
