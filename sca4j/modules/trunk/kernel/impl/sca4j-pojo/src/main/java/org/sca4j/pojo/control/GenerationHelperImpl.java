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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.pojo.control;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import org.sca4j.pojo.provision.PojoComponentDefinition;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.Signature;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * @version $Rev: 5246 $ $Date: 2008-08-20 22:30:18 +0100 (Wed, 20 Aug 2008) $
 */
public class GenerationHelperImpl implements InstanceFactoryGenerationHelper {

    public Integer getInitLevel(ComponentDefinition<?> definition, PojoComponentType type) {
        Integer initLevel = definition.getInitLevel();
        if (initLevel == null) {
            initLevel = type.getInitLevel();
        }
        return initLevel;
    }

    public Signature getSignature(Method method) {
        return method == null ? null : new Signature(method);
    }

    public void processInjectionSites(LogicalComponent<? extends Implementation<PojoComponentType>> component,
                                      InstanceFactoryDefinition providerDefinition) {

        Implementation<PojoComponentType> implementation = component.getDefinition().getImplementation();
        PojoComponentType type = implementation.getComponentType();
        Map<InjectionSite, InjectableAttribute> mappings = type.getInjectionSites();

        // add injections for all the active constructor args
        Map<InjectionSite, InjectableAttribute> construction = providerDefinition.getConstruction();
        Signature constructor = type.getConstructor();
        Set<InjectableAttribute> byConstruction = new HashSet<InjectableAttribute>(constructor.getParameterTypes().size());
        for (int i = 0; i < constructor.getParameterTypes().size(); i++) {
            InjectionSite site = new ConstructorInjectionSite(constructor, i);
            InjectableAttribute attribute = mappings.get(site);
            construction.put(site, attribute);
            byConstruction.add(attribute);
        }

        // add field/method injections
        Map<InjectionSite, InjectableAttribute> postConstruction = providerDefinition.getPostConstruction();
        Map<InjectionSite, InjectableAttribute> reinjection = providerDefinition.getReinjection();
        for (Map.Entry<InjectionSite, InjectableAttribute> entry : mappings.entrySet()) {
            InjectionSite site = entry.getKey();
            if (site instanceof ConstructorInjectionSite) {
                continue;
            }
            
            InjectableAttribute attribute = entry.getValue();
            if (!byConstruction.contains(attribute)) {
                postConstruction.put(site, attribute);
            }
            reinjection.put(site, attribute);
        }
    }

    public void processPropertyValues(LogicalComponent<?> component, PojoComponentDefinition physical) {
        for (Map.Entry<String, Document> entry : component.getPropertyValues().entrySet()) {
            String name = entry.getKey();
            Document value = entry.getValue();
            if (value != null) {
                physical.setPropertyValue(name, value);
            }
        }
    }
}
