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
package org.sca4j.jaxb.control.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Element;

import org.sca4j.jaxb.provision.AbstractTransformingInterceptorDefinition;
import org.sca4j.jaxb.provision.ReferenceTransformingInterceptorDefinition;
import org.sca4j.jaxb.provision.ServiceTransformingInterceptorDefinition;
import org.sca4j.jaxb.control.api.JAXBTransformationService;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;

import static org.sca4j.host.Namespaces.SCA4J_NS;

import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.InterceptorDefinitionGenerator;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * Generates interceptor definitions for operations marked with the JAXB intent.
 *
 * @version $Revision$ $Date$
 */
@Service(interfaces = {InterceptorDefinitionGenerator.class, JAXBTransformationService.class})
@EagerInit
public class TransformingInterceptorDefinitionGenerator implements InterceptorDefinitionGenerator, JAXBTransformationService {
    private static final QName INTENT_QNAME = new QName(SCA4J_NS, "jaxbPolicy");

    private GeneratorRegistry generatorRegistry;
    private Map<QName, QName> engagedBindings;

    public TransformingInterceptorDefinitionGenerator(@Reference GeneratorRegistry generatorRegistry) {
        this.generatorRegistry = generatorRegistry;
        engagedBindings = new HashMap<QName, QName>();
    }

    @Init
    public void init() {
        generatorRegistry.register(INTENT_QNAME, this);
    }

    public void registerBinding(QName name, QName dataType) {
        engagedBindings.put(name, dataType);
    }

    public AbstractTransformingInterceptorDefinition generate(Element policySet, Operation<?> operation, LogicalBinding<?> logicalBinding)
            throws GenerationException {
        QName dataType = engagedBindings.get(logicalBinding.getBinding().getType());
        if (dataType == null) {
            // The binding does not use JAXB, ignore. For example, a collocated wire may pass JAXB types but they do not need to be serialized
            // as invocations flow through the same VM.
            return null;
        }

        URI classLoaderId = logicalBinding.getParent().getParent().getClassLoaderId();
        Set<String> classNames = calculateParameterClassNames(operation);

        if (logicalBinding.getParent() instanceof LogicalService) {
            return new ServiceTransformingInterceptorDefinition(classLoaderId, dataType, classNames);
        } else {
            return new ReferenceTransformingInterceptorDefinition(classLoaderId, dataType, classNames);
        }
    }

    /**
     * Collates classnames for in and out parameters, faults, and return types on an operation.
     *
     * @param operation the operation
     * @return the collated class names
     */
    private Set<String> calculateParameterClassNames(Operation<?> operation) {
        Set<String> classNames = new HashSet<String>();
        List<? extends DataType<?>> inputTypes = operation.getInputType().getLogical();
        // parameter types
        for (DataType<?> inputType : inputTypes) {
            String className = ((Class<?>) inputType.getPhysical()).getName();
            classNames.add(className);
        }

        // fault types
        List<? extends DataType<?>> faultTypes = operation.getFaultTypes();
        for (DataType<?> faultType : faultTypes) {
            Class<?> faultClass = (Class<?>) faultType.getPhysical();
            classNames.add(faultClass.getName());
        }

        // return type
        DataType<?> returnType = operation.getOutputType();
        classNames.add(((Class<?>) returnType.getPhysical()).getName());
        return classNames;
    }

}
