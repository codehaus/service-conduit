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
package org.sca4j.java.control;

import org.sca4j.java.provision.JavaComponentDefinition;
import org.sca4j.java.provision.JavaWireSourceDefinition;
import org.sca4j.java.provision.JavaWireTargetDefinition;
import org.sca4j.java.scdl.JavaImplementation;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * Handles generation operations for Java components and specialized subtypes.
 *
 * @version $Revision$ $Date$
 */
public interface JavaGenerationHelper {

    JavaComponentDefinition generate(LogicalComponent<? extends JavaImplementation> component, JavaComponentDefinition physical)
            throws GenerationException;

    PhysicalWireSourceDefinition generateWireSource(LogicalComponent<? extends JavaImplementation> source,
                                                    JavaWireSourceDefinition wireDefinition,
                                                    LogicalReference reference,
                                                    Policy policy) throws GenerationException;

    PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<? extends JavaImplementation> source,
                                                            JavaWireSourceDefinition wireDefinition,
                                                            ServiceContract<?> serviceContract,
                                                            Policy policy) throws GenerationException;

    PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<? extends JavaImplementation> source,
                                                            LogicalResource<?> resource,
                                                            JavaWireSourceDefinition wireDefinition) throws GenerationException;

    PhysicalWireTargetDefinition generateWireTarget(LogicalService service,
                                                    LogicalComponent<? extends JavaImplementation> target,
                                                    JavaWireTargetDefinition wireDefinition,
                                                    Policy policy) throws GenerationException;
}
