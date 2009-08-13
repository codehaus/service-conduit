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
package org.sca4j.fabric.async;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.scdl.Operation;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.InterceptorDefinitionGenerator;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Element;

/**
 * Creates {@link NonBlockingInterceptorDefinition}s for one-way operations.
 *
 * @version $Rev: 2931 $ $Date: 2008-02-28 12:49:35 +0000 (Thu, 28 Feb 2008) $
 */
@EagerInit
public class NonBlockingGenerator implements InterceptorDefinitionGenerator {
    
    private static final QName QNAME = new QName(Namespaces.SCA4J_NS, "oneWayPolicy");
    
    private GeneratorRegistry registry;

    public NonBlockingGenerator(@Reference GeneratorRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void init() {
        registry.register(QNAME, this);
    }

    public PhysicalInterceptorDefinition generate(Element policyDefinition,
                                                  Operation<?> operation,
                                                  LogicalBinding<?> logicalBinding) throws GenerationException {
        return new NonBlockingInterceptorDefinition();
    }
}
