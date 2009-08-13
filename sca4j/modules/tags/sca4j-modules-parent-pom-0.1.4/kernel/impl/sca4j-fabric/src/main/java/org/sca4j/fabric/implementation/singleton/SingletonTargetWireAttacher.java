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

package org.sca4j.fabric.implementation.singleton;

import java.net.URI;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.EagerInit;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.Wire;

/**
 * Exists as a no-op attacher for system singleton components
 *
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
@EagerInit
public class SingletonTargetWireAttacher implements TargetWireAttacher<SingletonWireTargetDefinition> {
    private final ComponentManager manager;

    public SingletonTargetWireAttacher(@Reference ComponentManager manager) {
        this.manager = manager;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, SingletonWireTargetDefinition target, Wire wire)
            throws WiringException {
    }

    public ObjectFactory<?> createObjectFactory(SingletonWireTargetDefinition target) throws WiringException {
        URI targetId = UriHelper.getDefragmentedName(target.getUri());
        SingletonComponent<?> targetComponent = (SingletonComponent<?>) manager.getComponent(targetId);
        return targetComponent.createObjectFactory();
    }
}
