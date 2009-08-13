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
