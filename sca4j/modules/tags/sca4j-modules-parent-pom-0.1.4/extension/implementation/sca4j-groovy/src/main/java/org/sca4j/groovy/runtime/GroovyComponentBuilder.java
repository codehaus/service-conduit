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
package org.sca4j.groovy.runtime;

import java.net.URI;

import org.sca4j.pojo.builder.PojoComponentBuilder;
import org.sca4j.pojo.instancefactory.InstanceFactoryBuilderRegistry;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;
import org.sca4j.groovy.provision.GroovyComponentDefinition;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 5250 $ $Date: 2008-08-21 02:18:25 +0100 (Thu, 21 Aug 2008) $
 */
@EagerInit
public class GroovyComponentBuilder<T> extends PojoComponentBuilder<T, GroovyComponentDefinition, GroovyComponent<T>> {
    public GroovyComponentBuilder(@Reference ComponentBuilderRegistry builderRegistry,
                                  @Reference ScopeRegistry scopeRegistry,
                                  @Reference InstanceFactoryBuilderRegistry providerBuilders,
                                  @Reference ClassLoaderRegistry classLoaderRegistry,
                                  @Reference(name = "transformerRegistry")TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        super(builderRegistry, scopeRegistry, providerBuilders, classLoaderRegistry,transformerRegistry);
    }

    @Init
    public void init() {
        builderRegistry.register(GroovyComponentDefinition.class, this);
    }

    public GroovyComponent<T> build(GroovyComponentDefinition definition) throws BuilderException {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        URI groupId = definition.getGroupId();
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(definition.getClassLoaderId());

        // get the scope container for this component
        String scopeName = definition.getScope();
        Scope<?> scope = scopeRegistry.getScope(scopeName);
        ScopeContainer<?> scopeContainer = scopeRegistry.getScopeContainer(scope);

        // create the InstanceFactoryProvider based on the definition in the model
        InstanceFactoryDefinition providerDefinition = definition.getProviderDefinition();
        InstanceFactoryProvider<T> provider = providerBuilders.build(providerDefinition, classLoader);

        createPropertyFactories(definition, provider);

        return new GroovyComponent<T>(componentId, provider, scopeContainer, groupId, initLevel, -1, -1);
    }
}
