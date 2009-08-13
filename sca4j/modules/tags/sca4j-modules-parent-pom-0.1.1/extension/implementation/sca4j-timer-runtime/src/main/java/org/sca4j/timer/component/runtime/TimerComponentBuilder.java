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
package org.sca4j.timer.component.runtime;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.pojo.builder.PojoComponentBuilder;
import org.sca4j.pojo.component.PojoComponentContext;
import org.sca4j.pojo.component.PojoRequestContext;
import org.sca4j.pojo.injection.ConversationIDObjectFactory;
import org.sca4j.pojo.instancefactory.InstanceFactoryBuilderRegistry;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.SingletonObjectFactory;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.timer.component.provision.TimerComponentDefinition;
import org.sca4j.timer.component.provision.TriggerData;
import org.sca4j.timer.spi.TimerService;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class TimerComponentBuilder<T> extends PojoComponentBuilder<T, TimerComponentDefinition, TimerComponent<?>> {
    private TimerService nonTrxTimerService;
    private TimerService trxTimerService;
    private ProxyService proxyService;

    public TimerComponentBuilder(@Reference ComponentBuilderRegistry builderRegistry,
                                 @Reference ScopeRegistry scopeRegistry,
                                 @Reference InstanceFactoryBuilderRegistry providerBuilders,
                                 @Reference ClassLoaderRegistry classLoaderRegistry,
                                 @Reference(name = "transformerRegistry")TransformerRegistry<PullTransformer<?, ?>> transformerRegistry,
                                 @Reference ProxyService proxyService,
                                 @Reference(name = "nonTrxTimerService")TimerService nonTrxTimerService,
                                 @Reference(name = "trxTimerService")TimerService trxTimerService) {
        super(builderRegistry, scopeRegistry, providerBuilders, classLoaderRegistry, transformerRegistry);
        this.proxyService = proxyService;
        this.nonTrxTimerService = nonTrxTimerService;
        this.trxTimerService = trxTimerService;
    }

    @Init
    public void init() {
        builderRegistry.register(TimerComponentDefinition.class, this);
    }

    public TimerComponent<T> build(TimerComponentDefinition definition) throws BuilderException {
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
        TriggerData data = definition.getTriggerData();
        TimerService timerService;
        if (definition.isTransactional()) {
            timerService = trxTimerService;
        } else {
            timerService = nonTrxTimerService;
        }
        TimerComponent<T> component = new TimerComponent<T>(componentId,
                                                            provider,
                                                            scopeContainer,
                                                            groupId,
                                                            initLevel,
                                                            definition.getMaxIdleTime(),
                                                            definition.getMaxAge(),
                                                            proxyService,
                                                            data,
                                                            timerService);

        PojoRequestContext requestContext = new PojoRequestContext();
        provider.setObjectFactory(InjectableAttribute.REQUEST_CONTEXT, new SingletonObjectFactory<PojoRequestContext>(requestContext));

        PojoComponentContext componentContext = new PojoComponentContext(component, requestContext);
        provider.setObjectFactory(InjectableAttribute.COMPONENT_CONTEXT, new SingletonObjectFactory<PojoComponentContext>(componentContext));
        provider.setObjectFactory(InjectableAttribute.CONVERSATION_ID, new ConversationIDObjectFactory());

        return component;

    }

}
