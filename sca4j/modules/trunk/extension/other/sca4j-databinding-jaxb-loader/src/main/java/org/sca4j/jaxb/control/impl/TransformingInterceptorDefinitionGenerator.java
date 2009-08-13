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
