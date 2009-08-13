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
package org.sca4j.pojo.reflection;

import java.util.HashSet;
import java.util.Set;

import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceInitializationException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.invocation.WorkContextTunnel;

/**
 * @version $Rev: 5318 $ $Date: 2008-09-01 22:48:11 +0100 (Mon, 01 Sep 2008) $
 */
public class ReflectiveInstanceWrapper<T> implements InstanceWrapper<T> {
    private final T instance;
    private boolean reinjectable;
    private final ClassLoader cl;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
    private boolean started;
    private final InjectableAttribute[] attributes;
    private final Injector<T>[] injectors;
    private final Set<Injector<T>> updatedInjectors;

    public ReflectiveInstanceWrapper(T instance,
                                     boolean reinjectable,
                                     ClassLoader cl,
                                     EventInvoker<T> initInvoker,
                                     EventInvoker<T> destroyInvoker,
                                     InjectableAttribute[] attributes,
                                     Injector<T>[] injectors) {
        this.instance = instance;
        this.reinjectable = reinjectable;
        this.cl = cl;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.attributes = attributes;
        this.started = false;
        this.injectors = injectors;
        if (reinjectable) {
            this.updatedInjectors = new HashSet<Injector<T>>();
        } else {
            this.updatedInjectors = null;
        }
    }

    public T getInstance() {
        assert started;
        return instance;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(WorkContext workContext) throws InstanceInitializationException {
        assert !started;
        if (initInvoker != null) {
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            WorkContext oldContext = WorkContextTunnel.getThreadWorkContext();
            try {
                Thread.currentThread().setContextClassLoader(cl);
                WorkContextTunnel.setThreadWorkContext(workContext);
                initInvoker.invokeEvent(instance);
            } catch (ObjectCallbackException e) {
                throw new InstanceInitializationException(e.getMessage(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
                WorkContextTunnel.setThreadWorkContext(oldContext);
            }
        }
        started = true;
    }


    public void stop(WorkContext context) throws InstanceDestructionException {
        assert started;
        WorkContext oldWorkContext = WorkContextTunnel.getThreadWorkContext();
        try {
            if (destroyInvoker != null) {
                ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(cl);
                    WorkContextTunnel.setThreadWorkContext(context);
                    destroyInvoker.invokeEvent(instance);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldCl);
                    WorkContextTunnel.setThreadWorkContext(oldWorkContext);
                }
            }
        } catch (ObjectCallbackException e) {
            throw new InstanceDestructionException(e.getMessage(), e);
        } finally {
            started = false;
        }
    }


    public void reinject() throws InstanceLifecycleException {
        if (!reinjectable) {
            throw new IllegalStateException("Implementation is not reinjectable");
        }
        try {
            for (Injector<T> injector : updatedInjectors) {
                injector.inject(instance);
            }
            updatedInjectors.clear();
        } catch (ObjectCreationException ex) {
            throw new InstanceLifecycleException("Unable to inject", ex);
        }
    }

    public void addObjectFactory(String referenceName, ObjectFactory<?> factory, Object key) {
        for (int i = 0; i < attributes.length; i++) {
            InjectableAttribute attribute = attributes[i];
            if (attribute.getName().equals(referenceName)) {
                Injector<T> injector = injectors[i];
                injector.setObjectFactory(factory, key);
                if (instance != null && !reinjectable) {
                    throw new IllegalStateException("Implementation is not reinjectable");

                } else if (instance != null) {
                    updatedInjectors.add(injector);
                }
            }
        }

    }

}
