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
package org.sca4j.fabric.generator.component;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Property;

import org.sca4j.fabric.command.StartCompositeContextCommand;
import org.sca4j.spi.generator.AddCommandGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;

/**
 * Generates a command to start the composite context on a runtime.
 *
 * @version $Rev: 4150 $ $Date: 2008-05-09 20:33:01 +0100 (Fri, 09 May 2008) $
 */
@EagerInit
public class StartCompositeContextCommandGenerator implements AddCommandGenerator {
    private final int order;

    public StartCompositeContextCommandGenerator(@Property(name = "order")int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @SuppressWarnings("unchecked")
    public StartCompositeContextCommand generate(LogicalComponent<?> component) throws GenerationException {
        if (!component.isProvisioned() && component instanceof LogicalCompositeComponent) {
            return new StartCompositeContextCommand(order, component.getUri());
        }
        return null;

    }

}
