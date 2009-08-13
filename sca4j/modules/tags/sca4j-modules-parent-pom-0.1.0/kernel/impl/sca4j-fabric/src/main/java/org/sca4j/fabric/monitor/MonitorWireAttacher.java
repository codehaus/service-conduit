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

import org.osoa.sca.annotations.Reference;

import org.sca4j.monitor.MonitorFactory;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.SingletonObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.Wire;

/**
 * TargetWireAttacher that handles monitor resources.
 * <p/>
 * This only support optimized resources.
 *
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
public class MonitorWireAttacher implements TargetWireAttacher<MonitorWireTargetDefinition> {
    private final MonitorFactory monitorFactory;
    private final ClassLoaderRegistry classLoaderRegistry;

    public MonitorWireAttacher(@Reference MonitorFactory monitorFactory, @Reference ClassLoaderRegistry classLoaderRegistry) {
        this.monitorFactory = monitorFactory;
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, MonitorWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

    public ObjectFactory<?> createObjectFactory(MonitorWireTargetDefinition target) throws WiringException {
        try {
            Class<?> type = classLoaderRegistry.loadClass(target.getClassLoaderId(), target.getMonitorType());
            Object monitor = monitorFactory.getMonitor(type, target.getUri());
            return new SingletonObjectFactory<Object>(monitor);
        } catch (ClassNotFoundException e) {
            throw new WireAttachException("Unable to load monitor class: " + target.getMonitorType(), target.getUri(), null, e);
        }
    }
}
