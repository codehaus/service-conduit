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
package org.sca4j.spi.generator;

import java.util.List;

import javax.xml.namespace.QName;

import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * A registry for {@link ComponentGenerator}s, {@link BindingGenerator}s, and {@link InterceptorDefinitionGenerator}s . Generators are responsible for
 * producing physical model objects that are provisioned to service nodes from their logical counterparts.
 *
 * @version $Rev: 3641 $ $Date: 2008-04-15 10:52:29 +0100 (Tue, 15 Apr 2008) $
 */
public interface GeneratorRegistry {

    /**
     * Registers a component generator.
     *
     * @param clazz     the implementation type the generator handles
     * @param generator the generator to register
     */
    <T extends Implementation<?>> void register(Class<T> clazz, ComponentGenerator<LogicalComponent<T>> generator);

    /**
     * Unregisters a component generator.
     *
     * @param clazz     the implementation type the generator handles
     * @param generator the generator to unregister
     */
    <T extends Implementation<?>> void unregister(Class<T> clazz, ComponentGenerator<LogicalComponent<T>> generator);

    /**
     * Gets a component generator for the specified implementation.
     *
     * @param clazz the implementation type the generator handles.
     * @return a the component generator for that implementation type
     * @throws GeneratorNotFoundException if no generator is registered for the implementation type
     */
    <T extends Implementation<?>> ComponentGenerator<LogicalComponent<T>> getComponentGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Gets a binding generator for the specified binding class.
     *
     * @param clazz The binding type type the generator handles.
     * @return The registered binding generator.
     * @throws GeneratorNotFoundException if no generator is registered for the binding type
     */
    <T extends BindingDefinition> BindingGenerator<?, ?, T> getBindingGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Registers a resource wire generator.
     *
     * @param clazz     The resource type the generator handles.
     * @param generator The generator to register.
     */
    <T extends ResourceDefinition> void register(Class<T> clazz, ResourceWireGenerator<?, T> generator);

    /**
     * Unregisters a resource wire generator.
     *
     * @param clazz     the resource type the generator handles
     * @param generator the generator to register
     */
    <T extends ResourceDefinition> void unregister(Class<T> clazz, ResourceWireGenerator<?, T> generator);

    /**
     * Gets the resource wire generator for the resource type.
     *
     * @param clazz the resource type the generator handles
     * @return the registered resource wire generator
     * @throws GeneratorNotFoundException if no generator is registered for the resource type
     */
    <T extends ResourceDefinition> ResourceWireGenerator<?, T> getResourceWireGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Registers an interceptor generator by type.
     *
     * @param extensionName fully qualified name of the extension
     * @param generator     interceptor generator to register
     */
    void register(QName extensionName, InterceptorDefinitionGenerator generator);

    /**
     * Registers an interceptor generator by type.
     *
     * @param extensionName fully qualified name of the extension
     * @param generator     interceptor generator to register
     */
    void unregister(QName extensionName, InterceptorDefinitionGenerator generator);

    /**
     * Gets the interceptor definition generator for the qualified name.
     *
     * @param extensionName qualified name of the policy extension
     * @return Interceptor definition generator
     * @throws GeneratorNotFoundException if no generator is registered for the policy extension type
     */
    InterceptorDefinitionGenerator getInterceptorDefinitionGenerator(QName extensionName) throws GeneratorNotFoundException;

    /**
     * Registers a command generator.
     *
     * @param generator the generator to register
     */
    void register(CommandGenerator generator);

    /**
     * Unregisters a command generator.
     *
     * @param generator the generator to unregister
     */
    void unregister(CommandGenerator generator);

    /**
     * Gets all the registered command generators.
     *
     * @return All the registered command generators.
     */
    List<CommandGenerator> getCommandGenerators();

}
