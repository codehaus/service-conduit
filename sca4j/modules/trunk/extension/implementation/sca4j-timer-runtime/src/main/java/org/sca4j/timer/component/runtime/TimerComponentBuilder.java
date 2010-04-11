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
package org.sca4j.timer.component.runtime;

import java.net.URI;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
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
                                 @Reference(name = "transformerRegistry")TransformerRegistry<PullTransformer<?, ?>> transformerRegistry,
                                 @Reference ProxyService proxyService,
                                 @Reference(name = "nonTrxTimerService")TimerService nonTrxTimerService,
                                 @Reference(name = "trxTimerService")TimerService trxTimerService) {
        super(builderRegistry, scopeRegistry, providerBuilders, transformerRegistry);
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
        ClassLoader classLoader = getClass().getClassLoader();

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
