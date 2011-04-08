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
package org.sca4j.scdl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The definition of the configurable aspects of an implementation in terms of the services it exposes, the services it references, and properties
 * that can be used to configure it.
 * <p/>
 * A service represents an addressable interface provided by the implementation. Such a service may be the target of a wire from another component.
 * <p/>
 * A reference represents a requirement that an implementation has on a service provided by another component or by a resource outside the SCA system.
 * Such a reference may be the source of a wire to another component.
 * <p/>
 * A property allows the behaviour of the implementation to be configured through externally set values.
 * <p/>
 * A component type may also declare that it wishes to be initialized upon activation of the scope that contains it and may specify an order relative
 * to other eagerly initializing components. For example, an implementation that pre-loads some form of cache could declare that it should be eagerly
 * initialized at the start of the scope so that the cache load occured on startup rather than first use.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public abstract class AbstractComponentType<S extends ServiceDefinition, R extends ReferenceDefinition, P extends Property, RD extends ResourceDefinition> extends ModelObject {
    
    private String scope;
    private int initLevel;
    private long maxAge;
    private long maxIdleTime;
    private final Map<String, S> services = new HashMap<String, S>();
    private final Map<String, R> references = new HashMap<String, R>();
    private final Map<String, P> properties = new HashMap<String, P>();
    private final Map<String, RD> resources = new HashMap<String, RD>();

    protected AbstractComponentType() {
    }

    /**
     * Returns the lifecycle scope for the component.
     *
     * @return the lifecycle scope for the component
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the lifecycle scope for the component.
     *
     * @param scope the lifecycle scope for the component
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the default initialization level for components of this type. A value greater than zero indicates that components should be eagerly
     * initialized.
     *
     * @return the default initialization level
     */
    public int getInitLevel() {
        return initLevel;
    }

    /**
     * Sets the default initialization level for components of this type. A value greater than zero indicates that components should be eagerly
     * initialized.
     *
     * @param initLevel default initialization level for components of this type
     */
    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
    }

    /**
     * Returns true if this component should be eagerly initialized.
     *
     * @return true if this component should be eagerly initialized
     */
    public boolean isEagerInit() {
        return initLevel > 0;
    }

    /**
     * Returns the idle time allowed between operations in milliseconds if the implementation is conversational
     *
     * @return the idle time allowed between operations in milliseconds if the implementation is conversational
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * Sets the idle time allowed between operations in milliseconds if the implementation is conversational.
     *
     * @param maxIdleTime the idle time allowed between operations in milliseconds if the implementation is conversational
     */
    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    /**
     * Returns the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     *
     * @return the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the maximum age a conversation may remain active in milliseconds if the implementation is conversational.
     *
     * @param maxAge the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     */
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
    
    /**
     * Returns the names of all the services.
     * 
     * @return Names of all the services.
     */
    public Set<String> getServiceNames() {
        return services.keySet();
    }

    /**
     * Returns a live Map of the services provided by the implementation.
     *
     * @return a live Map of the services provided by the implementation
     */
    public Map<String, S> getServices() {
        return services;
    }

    /**
     * Add a service to those provided by the implementation. Any existing service with the same name is replaced.
     *
     * @param service a service provided by the implementation
     */
    public void add(S service) {
        services.put(service.getName(), service);
    }

    /**
     * Checks if this component type has a service with a certain name.
     *
     * @param name the name of the service to check
     * @return true if there is a service defined with that name
     */
    public boolean hasService(String name) {
        return services.containsKey(name);
    }

    /**
     * Returns a live Map of references to services consumed by the implementation.
     *
     * @return a live Map of references to services consumed by the implementation
     */
    public Map<String, R> getReferences() {
        return references;
    }

    /**
     * Add a reference to a service consumed by the implementation. Any existing reference with the same name is replaced.
     *
     * @param reference a reference to a service consumed by the implementation
     */
    public void add(R reference) {
        references.put(reference.getName(), reference);
    }

    /**
     * Checks if this component type has a reference with a certain name.
     *
     * @param name the name of the reference to check
     * @return true if there is a reference defined with that name
     */
    public boolean hasReference(String name) {
        return references.containsKey(name);
    }

    /**
     * Returns a live Map of properties that can be used to configure the implementation.
     *
     * @return a live Map of properties that can be used to configure the implementation
     */
    public Map<String, P> getProperties() {
        return properties;
    }

    /**
     * Add a property that can be used to configure the implementation. Any existing property with the same name is replaced.
     *
     * @param property a property that can be used to configure the implementation
     */
    public void add(P property) {
        properties.put(property.getName(), property);
    }

    /**
     * Checks if this component type has a property with a certain name.
     *
     * @param name the name of the property to check
     * @return true if there is a property defined with that name
     */
    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    /**
     * Returns a live Map of resoures that can be used to configure the implementation.
     *
     * @return a live Map of resources that can be used to configure the implementation
     */
    public Map<String, RD> getResources() {
        return resources;
    }

    /**
     * Add a resource that can be used to configure the implementation. Any existing resource with the same name is replaced.
     *
     * @param resource a resource that can be used to configure the implementation
     */
    public void add(RD resource) {
        resources.put(resource.getName(), resource);
    }

    /**
     * Checks if this component type has a resource with a certain name.
     *
     * @param name the name of the resource to check
     * @return true if there is a resource defined with that name
     */
    public boolean hasResource(String name) {
        return resources.containsKey(name);
    }

    @Override
    public void validate(ValidationContext context) {
        for (S s : services.values()) {
            s.validate(context);
        }
        for (R r : references.values()) {
            r.validate(context);
        }
        for (RD rd : resources.values()) {
            rd.validate(context);
        }
        for (P p : properties.values()) {
            p.validate(context);
        }
    }
}
