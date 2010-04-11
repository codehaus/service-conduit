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
package org.sca4j.web.control;

import static org.sca4j.container.web.spi.WebApplicationActivator.CONTEXT_ATTRIBUTE;
import static org.sca4j.web.provision.WebConstants.SERVLET_CONTEXT_SITE;
import static org.sca4j.web.provision.WebConstants.SESSION_CONTEXT_SITE;
import static org.sca4j.web.provision.WebContextInjectionSite.ContextType.SERVLET_CONTEXT;
import static org.sca4j.web.provision.WebContextInjectionSite.ContextType.SESSION_CONTEXT;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;
import org.sca4j.web.introspection.WebComponentType;
import org.sca4j.web.introspection.WebImplementation;
import org.sca4j.web.provision.WebComponentDefinition;
import org.sca4j.web.provision.WebComponentWireSourceDefinition;
import org.sca4j.web.provision.WebContextInjectionSite;
import org.w3c.dom.Document;

/**
 * Generates commands to provision a web component.
 *
 * @version $Rev: 2931 $ $Date: 2008-02-28 04:49:35 -0800 (Thu, 28 Feb 2008) $
 */
@EagerInit
public class WebComponentGenerator implements ComponentGenerator<LogicalComponent<WebImplementation>> {
    private HostInfo info;

    public WebComponentGenerator(@Reference GeneratorRegistry registry, @Reference HostInfo info) {
        this.info = info;
        registry.register(WebImplementation.class, this);
    }

    public PhysicalComponentDefinition generate(LogicalComponent<WebImplementation> component) throws GenerationException {
        ComponentDefinition<WebImplementation> definition = component.getDefinition();
        WebComponentType componentType = definition.getImplementation().getComponentType();
        URI componentId = component.getUri();
        // the context URL for the web application is derived from the component name relative to the domain
        String contextUrl = info.getDomain().relativize(componentId).toString();
        WebComponentDefinition physical = new WebComponentDefinition();
        physical.setComponentId(componentId);
        physical.setContextUrl(contextUrl);
        physical.setGroupId(component.getParent().getUri());
        Map<String, Map<String, InjectionSite>> sites = generateInjectionMapping(componentType);
        physical.setInjectionMappings(sites);
        processPropertyValues(component, physical);
        URI classLoaderId = component.getClassLoaderId();
        physical.setClassLoaderId(classLoaderId);
        return physical;
    }

    public WebComponentWireSourceDefinition generateWireSource(LogicalComponent<WebImplementation> source, LogicalReference reference, Policy policy)
            throws GenerationException {

        WebComponentWireSourceDefinition sourceDefinition = new WebComponentWireSourceDefinition();
        sourceDefinition.setUri(reference.getUri());
        if (reference.getDefinition().getServiceContract().isConversational()) {
            sourceDefinition.setInteractionType(InteractionType.CONVERSATIONAL);
        }
        return sourceDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<WebImplementation> source,
                                                                   ServiceContract<?> serviceContract,
                                                                   Policy policy) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service, LogicalComponent<WebImplementation> component, Policy policy)
            throws GenerationException {
        return null;
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<WebImplementation> source, LogicalResource<?> resource)
            throws GenerationException {
        return null;
    }

    private Map<String, Map<String, InjectionSite>> generateInjectionMapping(WebComponentType type) {
        Map<String, Map<String, InjectionSite>> mappings = new HashMap<String, Map<String, InjectionSite>>();
        for (ReferenceDefinition definition : type.getReferences().values()) {
            generateReferenceInjectionMapping(definition, type, mappings);
        }
        for (Property property : type.getProperties().values()) {
            generatePropertyInjectionMapping(property, mappings);
        }
        generateContextInjectionMapping(type, mappings);
        return mappings;
    }

    private void generateReferenceInjectionMapping(ReferenceDefinition definition,
                                                   WebComponentType type,
                                                   Map<String, Map<String, InjectionSite>> mappings) {
        Map<String, InjectionSite> mapping = mappings.get(definition.getName());
        if (mapping == null) {
            mapping = new HashMap<String, InjectionSite>();
            mappings.put(definition.getName(), mapping);
        }
        for (Map.Entry<String, Map<InjectionSite, InjectableAttribute>> entry : type.getInjectionSites().entrySet()) {
            for (Map.Entry<InjectionSite, InjectableAttribute> siteMap : entry.getValue().entrySet()) {
                if (siteMap.getValue().getName().equals(definition.getName())) {
                    mapping.put(entry.getKey(), siteMap.getKey());
                }
            }
        }
        ServiceContract<?> contract = definition.getServiceContract();
        String interfaceClass = contract.getQualifiedInterfaceName();
        // inject the reference into the session context
        WebContextInjectionSite site = new WebContextInjectionSite(interfaceClass, SESSION_CONTEXT);
        mapping.put(SESSION_CONTEXT_SITE, site);
        if (!contract.isConversational()) {
            // if the target service is non-conversational, also inject the reference into the servlet context
            WebContextInjectionSite servletContextsite = new WebContextInjectionSite(interfaceClass, SERVLET_CONTEXT);
            mapping.put(SERVLET_CONTEXT_SITE, servletContextsite);
        }
    }

    private void generatePropertyInjectionMapping(Property property, Map<String, Map<String, InjectionSite>> mappings) {
        Map<String, InjectionSite> mapping = mappings.get(property.getName());
        if (mapping == null) {
            mapping = new HashMap<String, InjectionSite>();
            mappings.put(property.getName(), mapping);
        }
        // inject the property into the session context
        // we don't need to do the type mappings from schema to Java so set Object as the type
        WebContextInjectionSite site = new WebContextInjectionSite(Object.class.getName(), SERVLET_CONTEXT);
        mapping.put(SESSION_CONTEXT_SITE, site);
    }

    private void generateContextInjectionMapping(WebComponentType type, Map<String, Map<String, InjectionSite>> mappings) {
        Map<String, InjectionSite> mapping = mappings.get(CONTEXT_ATTRIBUTE);
        if (mapping == null) {
            mapping = new HashMap<String, InjectionSite>();
            WebContextInjectionSite site =
                    new WebContextInjectionSite(ComponentContext.class.getName(), SESSION_CONTEXT);
            mapping.put(SESSION_CONTEXT_SITE, site);
            mappings.put(CONTEXT_ATTRIBUTE, mapping);
        }
        for (Map.Entry<String, Map<InjectionSite, InjectableAttribute>> entry : type.getInjectionSites().entrySet()) {
            for (Map.Entry<InjectionSite, InjectableAttribute> siteMap : entry.getValue().entrySet()) {
                if (siteMap.getValue().equals(InjectableAttribute.COMPONENT_CONTEXT)) {
                    mapping.put(entry.getKey(), siteMap.getKey());
                }
            }
        }

    }

    private void processPropertyValues(LogicalComponent<?> component, WebComponentDefinition physical) {
        for (Map.Entry<String, Document> entry : component.getPropertyValues().entrySet()) {
            String name = entry.getKey();
            Document value = entry.getValue();
            if (value != null) {
                physical.setPropertyValue(name, value);
            }
        }
    }


}
