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
package org.sca4j.fabric.monitor;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.scdl.MonitorResource;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.ResourceWireGenerator;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalResource;

/**
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
@EagerInit
public class MonitorWireGenerator implements ResourceWireGenerator<MonitorWireTargetDefinition, MonitorResource> {

    private final GeneratorRegistry registry;

    public MonitorWireGenerator(@Reference(name = "registry")GeneratorRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void init() {
        registry.register(MonitorResource.class, this);
    }

    @Destroy
    public void destroy() {
        registry.unregister(MonitorResource.class, this);
    }

    public MonitorWireTargetDefinition generateWireTargetDefinition(LogicalResource<MonitorResource> resource) throws GenerationException {

        LogicalComponent<?> component = resource.getParent();

        MonitorWireTargetDefinition definition = new MonitorWireTargetDefinition();
        definition.setMonitorType(resource.getResourceDefinition().getServiceContract().getQualifiedInterfaceName());
        definition.setUri(component.getUri());
        definition.setOptimizable(true);
        definition.setClassLoaderId(component.getClassLoaderId());

        return definition;
    }
}
