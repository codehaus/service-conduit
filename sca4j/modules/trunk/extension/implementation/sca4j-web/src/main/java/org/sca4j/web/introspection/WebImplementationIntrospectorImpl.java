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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.web.introspection;

import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.ReferenceDefinition;

/**
 * Default implementation of WebImplementationIntrospector.
 *
 * @version $Rev: 4352 $ $Date: 2008-05-25 22:51:33 +0100 (Sun, 25 May 2008) $
 */
public class WebImplementationIntrospectorImpl implements WebImplementationIntrospector {
    private ClassWalker<WebArtifactImplementation> classWalker;
    private IntrospectionHelper helper;
    private WebXmlIntrospector xmlIntrospector;

    public WebImplementationIntrospectorImpl(@Reference(name = "classWalker")ClassWalker<WebArtifactImplementation> classWalker,
                                             @Reference(name = "xmlIntrospector")WebXmlIntrospector xmlIntrospector,
                                             @Reference(name = "helper")IntrospectionHelper helper) {
        this.classWalker = classWalker;
        this.helper = helper;
        this.xmlIntrospector = xmlIntrospector;
    }

    public void introspect(WebImplementation implementation, IntrospectionContext context) {
        WebComponentType componentType = new WebComponentType();
        componentType.setScope("STATELESS");
        implementation.setComponentType(componentType);
        // load the servlet, filter and context listener classes referenced in the web.xml descriptor
        List<Class<?>> artifacts = xmlIntrospector.introspectArtifactClasses(context);
        for (Class<?> artifact : artifacts) {
            // introspect each class and generate a component type that will be merged into the web component type
            WebArtifactImplementation artifactImpl = new WebArtifactImplementation();
            PojoComponentType type = new PojoComponentType(artifact.getName());
            artifactImpl.setComponentType(type);
            TypeMapping typeMapping = helper.mapTypeParameters(artifact);
            IntrospectionContext childContext = new DefaultIntrospectionContext(context, typeMapping);
            classWalker.walk(artifactImpl, artifact, childContext);
            if (childContext.hasErrors()) {
                context.addErrors(childContext.getErrors());
            }
            if (childContext.hasWarnings()) {
                context.addWarnings(childContext.getWarnings());
            }
            validateComponentType(type, context);
            // TODO apply heuristics
            mergeComponentTypes(implementation.getComponentType(), type, context);
        }
    }

    private void validateComponentType(PojoComponentType type, IntrospectionContext context) {
        for (ReferenceDefinition reference : type.getReferences().values()) {
            if (reference.getServiceContract().isConversational()) {
                IllegalConversationalReferenceInjection failure = new IllegalConversationalReferenceInjection(reference, type.getImplClass());
                context.addError(failure);
            }

        }
    }

    /**
     * Merges the POJO component type into the web component type.
     *
     * @param webType  the web component type to merge into
     * @param pojoType the POJO component to merge
     * @param context  the introspection context
     */
    private void mergeComponentTypes(WebComponentType webType, PojoComponentType pojoType, IntrospectionContext context) {
        for (Map.Entry<String, ReferenceDefinition> entry : pojoType.getReferences().entrySet()) {
            String name = entry.getKey();
            ReferenceDefinition reference = webType.getReferences().get(name);
            if (reference != null) {
                if (!reference.getServiceContract().isAssignableFrom(entry.getValue().getServiceContract())) {
                    // TODO display areas where it was not matching
                    IncompatibleReferenceDefinitions failure = new IncompatibleReferenceDefinitions(name);
                    context.addError(failure);
                }

            } else {
                webType.add(entry.getValue());
            }
        }
        // apply all injection sites
        for (Map.Entry<InjectionSite, InjectableAttribute> entry : pojoType.getInjectionSites().entrySet()) {
            webType.addMapping(pojoType.getImplClass(), entry.getKey(), entry.getValue());
        }
    }

}
