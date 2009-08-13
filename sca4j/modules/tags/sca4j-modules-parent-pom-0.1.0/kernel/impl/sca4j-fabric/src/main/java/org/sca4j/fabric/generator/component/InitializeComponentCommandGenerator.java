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

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Property;

import org.sca4j.fabric.command.ComponentInitializationUri;
import org.sca4j.fabric.command.InitializeComponentCommand;
import org.sca4j.spi.generator.AddCommandGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;

/**
 * Generates a command to initialize an atomic component marked to eagerly initialize.
 *
 * @version $Rev: 2767 $ $Date: 2008-02-15 13:29:02 +0000 (Fri, 15 Feb 2008) $
 */
@EagerInit
public class InitializeComponentCommandGenerator implements AddCommandGenerator {
    private final int order;

    public InitializeComponentCommandGenerator(@Property(name = "order")int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @SuppressWarnings("unchecked")
    public InitializeComponentCommand generate(LogicalComponent<?> component) throws GenerationException {
        if (!(component instanceof LogicalCompositeComponent) && !component.isProvisioned() && component.isEagerInit()) {
            URI groupId = URI.create(component.getParent().getUri().toString() + "/");
            ComponentInitializationUri uri = new ComponentInitializationUri(groupId, component.getUri());
            return new InitializeComponentCommand(order, uri);
        }
        return null;
    }

}
