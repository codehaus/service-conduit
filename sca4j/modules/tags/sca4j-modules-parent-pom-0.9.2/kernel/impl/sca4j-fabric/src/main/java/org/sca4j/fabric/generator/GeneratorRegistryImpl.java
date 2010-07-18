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
package org.sca4j.fabric.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.CommandGenerator;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GeneratorNotFoundException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.InterceptorDefinitionGenerator;
import org.sca4j.spi.generator.ResourceWireGenerator;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * @version $Rev: 3656 $ $Date: 2008-04-17 01:44:36 +0100 (Thu, 17 Apr 2008) $
 */
public class GeneratorRegistryImpl implements GeneratorRegistry {

    private Map<Class<?>, ComponentGenerator<? extends LogicalComponent<? extends Implementation<?>>>> componentGenerators =
        new ConcurrentHashMap<Class<?>, ComponentGenerator<? extends LogicalComponent<? extends Implementation<?>>>>();
    
    private Map<Class<? extends BindingDefinition>, BindingGenerator<?, ?, ? extends BindingDefinition>> bindingGenerators =
        new ConcurrentHashMap<Class<? extends BindingDefinition>, BindingGenerator<?, ?, ? extends BindingDefinition>>();
    
    private List<CommandGenerator> commandGenerators = new ArrayList<CommandGenerator>();
    
    private Map<QName, InterceptorDefinitionGenerator> interceptorDefinitionGenerators = 
        new ConcurrentHashMap<QName, InterceptorDefinitionGenerator>();
    
    private Map<Class<? extends ResourceDefinition>, ResourceWireGenerator<?, ? extends ResourceDefinition>> resourceWireGenerators =
        new ConcurrentHashMap<Class<? extends ResourceDefinition>, ResourceWireGenerator<?, ? extends ResourceDefinition>>();
    
    @Reference(required = false)
    public void setBindingGenerators(Map<Class<? extends BindingDefinition>, BindingGenerator<?, ?, ? extends BindingDefinition>> bindingGenerators) {
        this.bindingGenerators = bindingGenerators;
    }

    public <T extends Implementation<?>> void register(Class<T> clazz, ComponentGenerator<LogicalComponent<T>> generator) {
        componentGenerators.put(clazz, generator);
    }

    public <T extends Implementation<?>> void unregister(Class<T> clazz, ComponentGenerator<LogicalComponent<T>> generator) {
        componentGenerators.remove(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends Implementation<?>> ComponentGenerator<LogicalComponent<T>> getComponentGenerator(Class<T> clazz)  
        throws GeneratorNotFoundException {
        if (!componentGenerators.containsKey(clazz)) {
            throw new GeneratorNotFoundException(clazz);
        }
        return (ComponentGenerator<LogicalComponent<T>>) componentGenerators.get(clazz);
    }
    
    public <T extends BindingDefinition> void register(Class<T> clazz, BindingGenerator<?, ?, T> generator) {
        bindingGenerators.put(clazz, generator);
    }

    public <T extends BindingDefinition> void unregister(Class<T> clazz, BindingGenerator<?, ?, T> generator) {
        bindingGenerators.remove(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends BindingDefinition> BindingGenerator<?, ?, T> getBindingGenerator(Class<T> clazz) 
        throws GeneratorNotFoundException {        
        if (!bindingGenerators.containsKey(clazz)) {
            throw new GeneratorNotFoundException(clazz);
        }
        return (BindingGenerator<?, ?, T>) bindingGenerators.get(clazz);
    }

    public <T extends ResourceDefinition> void register(Class<T> clazz, ResourceWireGenerator<?, T> generator) {
        resourceWireGenerators.put(clazz, generator);
    }

    public <T extends ResourceDefinition> void unregister(Class<T> clazz, ResourceWireGenerator<?, T> generator) {
        resourceWireGenerators.remove(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceDefinition> ResourceWireGenerator<?, T> getResourceWireGenerator(Class<T> clazz) 
        throws GeneratorNotFoundException {      
        if (!resourceWireGenerators.containsKey(clazz)) {
            throw new GeneratorNotFoundException(clazz);
        }
        return (ResourceWireGenerator<?, T>) resourceWireGenerators.get(clazz);
    }
    
    public void register(QName extensionName, InterceptorDefinitionGenerator interceptorDefinitionGenerator) {
        interceptorDefinitionGenerators.put(extensionName, interceptorDefinitionGenerator);
    }

    public void unregister(QName extensionName, InterceptorDefinitionGenerator generator) {
        interceptorDefinitionGenerators.remove(extensionName);
    }

    public InterceptorDefinitionGenerator getInterceptorDefinitionGenerator(QName extensionName)
        throws GeneratorNotFoundException {
        if (!interceptorDefinitionGenerators.containsKey(extensionName)) {
            throw new GeneratorNotFoundException(extensionName);
        }
        return interceptorDefinitionGenerators.get(extensionName);
    }
    
    public void register(CommandGenerator generator) {
        commandGenerators.add(generator);
    }

    public void unregister(CommandGenerator generator) {
        commandGenerators.remove(generator);
    }

    public List<CommandGenerator> getCommandGenerators() {
        return commandGenerators;
    }

}
