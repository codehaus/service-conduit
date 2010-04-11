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
package org.sca4j.web.runtime;

import static org.sca4j.container.web.spi.WebApplicationActivator.CONTEXT_ATTRIBUTE;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.sca4j.container.web.spi.WebApplicationActivationException;
import org.sca4j.container.web.spi.WebApplicationActivator;
import org.sca4j.pojo.reflection.Injector;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.PropertyValue;
import org.sca4j.spi.AbstractLifecycle;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.SingletonObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.wire.Wire;

/**
 * A component whose implementation is a web applicaiton.
 *
 * @version $Rev: 3020 $ $Date: 2008-03-03 19:16:33 -0800 (Mon, 03 Mar 2008) $
 */
public class WebComponent<T> extends AbstractLifecycle implements AtomicComponent<T> {

    private final URI uri;
    private final URI classLoaderId;
    private ClassLoader classLoader;
    private InjectorFactory injectorFactory;
    private final WebApplicationActivator activator;
    // injection site name to <artifact name, injection site>
    private final Map<String, Map<String, InjectionSite>> siteMappings;
    private final ProxyService proxyService;
    private final URI groupId;
    private final Map<String, ObjectFactory<?>> propertyFactories;
    private final Map<String, ObjectFactory<?>> referenceFactories;
    private final URI archiveUri;
    private ComponentContext context;
    private String contextUrl;

    public WebComponent(URI uri,
                        String contextUrl,
                        URI classLoaderId,
                        URI groupId,
                        URI archiveUri,
                        ClassLoader classLoader,
                        InjectorFactory injectorFactory,
                        WebApplicationActivator activator,
                        ProxyService proxyService,
                        Map<String, ObjectFactory<?>> propertyFactories,
                        Map<String, Map<String, InjectionSite>> injectorMappings) throws WebComponentCreationException {
        this.uri = uri;
        this.contextUrl = contextUrl;
        this.classLoaderId = classLoaderId;
        this.archiveUri = archiveUri;
        this.classLoader = classLoader;
        this.injectorFactory = injectorFactory;
        this.activator = activator;
        this.siteMappings = injectorMappings;
        this.proxyService = proxyService;
        this.groupId = groupId;
        this.propertyFactories = propertyFactories;
        referenceFactories = new ConcurrentHashMap<String, ObjectFactory<?>>();
    }

    public URI getUri() {
        return uri;
    }

    public void start() {
        try {
            Map<String, List<Injector<?>>> injectors = new HashMap<String, List<Injector<?>>>();
            injectorFactory.createInjectorMappings(injectors, siteMappings, referenceFactories, classLoader);
            injectorFactory.createInjectorMappings(injectors, siteMappings, propertyFactories, classLoader);
            context = new WebComponentContext(this);
            Map<String, ObjectFactory<?>> contextFactories = new HashMap<String, ObjectFactory<?>>();
            SingletonObjectFactory<ComponentContext> componentContextFactory = new SingletonObjectFactory<ComponentContext>(context);
            contextFactories.put(CONTEXT_ATTRIBUTE, componentContextFactory);
            injectorFactory.createInjectorMappings(injectors, siteMappings, contextFactories, classLoader);
            // activate the web application
            activator.activate(contextUrl, archiveUri, classLoaderId, injectors, context);
        } catch (InjectionCreationException e) {
            throw new WebComponentStartException("Error starting web component: " + uri.toString(), e);
        } catch (WebApplicationActivationException e) {
            throw new WebComponentStartException("Error starting web component: " + uri.toString(), e);
        }

    }

    public void stop() {
    }

    public Map<String, PropertyValue> getDefaultPropertyValues() {
        return null;
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue> defaultPropertyValues) {

    }

    public void attachWire(String name, InteractionType interactionType, Wire wire) throws ObjectCreationException {
        Map<String, InjectionSite> sites = siteMappings.get(name);
        if (sites == null || sites.isEmpty()) {
            throw new ObjectCreationException("Injection site not found for: " + name);
        }
        Class<?> type;
        try {
            type = classLoader.loadClass(sites.values().iterator().next().getType());
        } catch (ClassNotFoundException e) {
            throw new ObjectCreationException("Reference type not found for: " + name, e);
        }
        ObjectFactory<?> factory = createWireFactory(type, interactionType, wire);
        attachWire(name, factory);
    }

    public void attachWire(String name, ObjectFactory<?> factory) throws ObjectCreationException {
        referenceFactories.put(name, factory);
    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, InteractionType interactionType, Wire wire) {
        return proxyService.createObjectFactory(interfaze, interactionType, wire, null);
    }

    public URI getGroupId() {
        return groupId;
    }

    public boolean isEagerInit() {
        return false;
    }

    public int getInitLevel() {
        return 0;
    }

    public long getMaxIdleTime() {
        return 0;
    }

    public long getMaxAge() {
        return 0;
    }

    public InstanceWrapper<T> createInstanceWrapper(WorkContext workContext) throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public ObjectFactory<T> createObjectFactory() {
        throw new UnsupportedOperationException();
    }

    public <R> ObjectFactory<R> createObjectFactory(Class<R> type, String serviceName) throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public <B> B getProperty(Class<B> type, String propertyName) throws ObjectCreationException {
        ObjectFactory<?> factory = propertyFactories.get(propertyName);
        if (factory != null) {
            return type.cast(factory.getInstance());
        } else {
            return null;
        }
    }

    public <B> B getService(Class<B> type, String name) throws ObjectCreationException {
        ObjectFactory<?> factory = referenceFactories.get(name);
        if (factory == null) {
            return null;
        } else {
            return type.cast(factory.getInstance());
        }
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> type, String name) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({"unchecked"})
    public <B, R extends CallableReference<B>> R cast(B target) {
        return (R) proxyService.cast(target);
    }

    public String toString() {
        return "[" + uri.toString() + "] in state [" + super.toString() + ']';
    }


}
