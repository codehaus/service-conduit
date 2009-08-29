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
package org.sca4j.scdl;

import java.util.HashMap;
import java.util.Map;

/**
 * A component type associated with an implementation that supports injection.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class InjectingComponentType extends AbstractComponentType<ServiceDefinition, ReferenceDefinition, Property, ResourceDefinition> {
    private static final long serialVersionUID = -2602867276842414240L;

    private final Map<InjectionSite, InjectableAttribute> injectionSites = new HashMap<InjectionSite, InjectableAttribute>();
    private Signature constructor;
    private Signature initMethod;
    private Signature destroyMethod;
    private final Map<String, CallbackDefinition> callbacks = new HashMap<String, CallbackDefinition>();

    /**
     * Default constructor.
     */
    public InjectingComponentType() {
    }

    /**
     * Add a reference and associate with an injection site.
     *
     * @param reference     the reference to add
     * @param injectionSite the injection site for the reference
     */
    public void add(ReferenceDefinition reference, InjectionSite injectionSite) {
        super.add(reference);
        InjectableAttribute injectableAttribute = new InjectableAttribute(InjectableAttributeType.REFERENCE, reference.getName());
        addInjectionSite(injectableAttribute, injectionSite);
    }

    /**
     * Add a property and associate with an injection site.
     *
     * @param property      the property to add
     * @param injectionSite the injection site for the property
     */
    public void add(Property property, InjectionSite injectionSite) {
        super.add(property);
        InjectableAttribute injectableAttribute = new InjectableAttribute(InjectableAttributeType.PROPERTY, property.getName());
        addInjectionSite(injectableAttribute, injectionSite);
    }

    /**
     * Add a resource and associate with an injection site.
     *
     * @param resource      the resource to add
     * @param injectionSite the injection site for the resource
     */
    public void add(ResourceDefinition resource, InjectionSite injectionSite) {
        super.add(resource);
        InjectableAttribute injectableAttribute = new InjectableAttribute(InjectableAttributeType.RESOURCE, resource.getName());
        addInjectionSite(injectableAttribute, injectionSite);
    }

    /**
     * Adds a callback proxy defintion and its associated injection site
     *
     * @param definition    the callback proxy definition
     * @param injectionSite the proxy injection site
     */
    public void add(CallbackDefinition definition, InjectionSite injectionSite) {
        String name = definition.getName();
        callbacks.put(name, definition);
        InjectableAttribute injectableAttribute = new InjectableAttribute(InjectableAttributeType.CALLBACK, name);
        addInjectionSite(injectableAttribute, injectionSite);
    }

    /**
     * Returns a collection of defined callback proxy definitions keyed by name
     *
     * @return the collection of proxy definitions
     */
    public Map<String, CallbackDefinition> getCallbacks() {
        return callbacks;
    }

    /**
     * Add the injection site for an injectable value.
     *
     * @param source the value to be injected
     * @param site   the injection site
     */
    public void addInjectionSite(InjectableAttribute source, InjectionSite site) {
        injectionSites.put(site, source);
    }

    /**
     * Returns the map of all injection mappings.
     *
     * @return the map of all injection mappings
     */
    public Map<InjectionSite, InjectableAttribute> getInjectionSites() {
        return injectionSites;
    }

    /**
     * Returns the signature of the constructor to use.
     *
     * @return the signature of the constructor to use
     */
    public Signature getConstructor() {
        return constructor;
    }

    /**
     * Sets the signature of the constructor to use.
     * @param constructor the signature of the constructor to use
     */
    public void setConstructor(Signature constructor) {
        this.constructor = constructor;
    }

    /**
     * Returns the component initializer method.
     *
     * @return the component initializer method
     */
    public Signature getInitMethod() {
        return initMethod;
    }

    /**
     * Sets the component initializer method.
     *
     * @param initMethod the component initializer method
     */
    public void setInitMethod(Signature initMethod) {
        this.initMethod = initMethod;
    }

    /**
     * Returns the component destructor method.
     *
     * @return the component destructor method
     */
    public Signature getDestroyMethod() {
        return destroyMethod;
    }

    /**
     * Sets the component destructor method.
     *
     * @param destroyMethod the component destructor method
     */
    public void setDestroyMethod(Signature destroyMethod) {
        this.destroyMethod = destroyMethod;
    }


}
