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
package org.sca4j.fabric.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Reference;

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
