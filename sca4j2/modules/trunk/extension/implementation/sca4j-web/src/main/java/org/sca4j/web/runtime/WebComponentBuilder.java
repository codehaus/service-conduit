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
package org.sca4j.web.runtime;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.container.web.spi.WebApplicationActivator;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilder;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.web.provision.WebComponentDefinition;

/**
 * Instantiates a web component on a runtime node.
 */
@EagerInit
public class WebComponentBuilder implements ComponentBuilder<WebComponentDefinition, WebComponent> {
    private WebApplicationActivator activator;
    private InjectorFactory injectorFactory;
    private ProxyService proxyService;
    private ComponentBuilderRegistry builderRegistry;

    public WebComponentBuilder(@Reference ProxyService proxyService,
                               @Reference ComponentBuilderRegistry registry,
                               @Reference WebApplicationActivator activator,
                               @Reference InjectorFactory injectorFactory) {
        this.proxyService = proxyService;
        this.builderRegistry = registry;
        this.activator = activator;
        this.injectorFactory = injectorFactory;
    }

    @Init
    public void init() {
        builderRegistry.register(WebComponentDefinition.class, this);
    }

    @Destroy
    public void destroy() {
    }

    public WebComponent build(WebComponentDefinition definition) throws BuilderException {
        URI componentId = definition.getComponentId();
        URI groupId = definition.getGroupId();
        // TODO fix properties
        Map<String, ObjectFactory<?>> propertyFactories = Collections.emptyMap();
        URI classLoaderId = definition.getClassLoaderId();
        Map<String, Map<String, InjectionSite>> injectorMappings = definition.getInjectionSiteMappings();
        ClassLoader cl = activator.getWebComponentClassLoader(classLoaderId);
        URI archiveUri = definition.getContributionUri();
        String contextUrl = definition.getContextUrl();
        return new WebComponent(componentId,
                                contextUrl,
                                classLoaderId,
                                groupId,
                                archiveUri,
                                cl,
                                injectorFactory,
                                activator,
                                proxyService,
                                propertyFactories,
                                injectorMappings);
    }

}
