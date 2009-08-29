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
package org.sca4j.binding.ws.axis2.control.jaxb;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.WebFault;

import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.InterceptorDefinitionGenerator;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.binding.ws.axis2.provision.jaxb.JaxbInterceptorDefinition;
import org.sca4j.host.Namespaces;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class JaxbInterceptorDefinitionGenerator implements InterceptorDefinitionGenerator {

    private static final QName EXTENSION_NAME = new QName(Namespaces.SCA4J_NS, "dataBinding.jaxb");
    
    private GeneratorRegistry generatorRegistry;

    public JaxbInterceptorDefinitionGenerator(@Reference GeneratorRegistry generatorRegistry) {
        this.generatorRegistry = generatorRegistry;
    }

    /**
     * Registers with the registry.
     */
    @Init
    public void init() {
        generatorRegistry.register(EXTENSION_NAME, this);
    }

    public JaxbInterceptorDefinition generate(Element policySet, Operation<?> operation, LogicalBinding<?> logicalBinding)
        throws GenerationException {
        
        boolean service = logicalBinding.getParent() instanceof LogicalService;
        
        URI classLoaderId = logicalBinding.getParent().getParent().getClassLoaderId();
        
        // This assumes a Java interface contract

        List<? extends DataType<?>> inputTypes = operation.getInputType().getLogical();
        List<? extends DataType<?>> faultTypes = operation.getFaultTypes();
        DataType<?> outputType = operation.getOutputType();

        Set<String> classNames = new HashSet<String>(inputTypes.size() + 1);

        // parameter types
        for (DataType<?> inputType : inputTypes) {
            String className = ((Class<?>) inputType.getPhysical()).getName();
            classNames.add(className);
        }

        // fault types
        Set<String> faultNames = new HashSet<String>(faultTypes.size());
        for (DataType<?> faultType : faultTypes) {
            Class<?> webFaultClass = (Class<?>) faultType.getPhysical();

            // in JAX-WS, the fault class is a wrapper for the fault message
            // the actual fault is returned by the getFaultInfo() method
            if (!webFaultClass.isAnnotationPresent(WebFault.class)) {
                throw new InvalidWebFaultException(webFaultClass.getName());
            }
            Method getFaultInfo;
            try {
                getFaultInfo = webFaultClass.getMethod("getFaultInfo");
            } catch (NoSuchMethodException e) {
                throw new MissingFaultInfoException(webFaultClass.getName());
            }
            Class<?> faultClass = getFaultInfo.getReturnType();

            faultNames.add(webFaultClass.getName());
            classNames.add(faultClass.getName());
        }

        // return type
        classNames.add(((Class<?>) outputType.getPhysical()).getName());

        return new JaxbInterceptorDefinition(classLoaderId, classNames, faultNames, service);

    }

}
