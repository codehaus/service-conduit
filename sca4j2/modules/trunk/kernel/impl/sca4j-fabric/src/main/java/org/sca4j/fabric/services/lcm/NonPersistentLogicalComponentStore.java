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
package org.sca4j.fabric.services.lcm;

import java.net.URI;

import org.sca4j.host.runtime.HostInfo;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.services.lcm.LogicalComponentStore;
import org.sca4j.spi.services.lcm.RecoveryException;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

/**
 * A non-persistent LogicalComponentStore
 *
 * @version $Rev: 4792 $ $Date: 2008-06-08 18:00:07 +0100 (Sun, 08 Jun 2008) $
 */
public class NonPersistentLogicalComponentStore implements LogicalComponentStore {
    private URI domainUri;
    private Autowire autowire = Autowire.OFF;

    public NonPersistentLogicalComponentStore(URI domainUri, Autowire autowire) {
        this.domainUri = domainUri;
        this.autowire = autowire;
    }

    @Constructor
    public NonPersistentLogicalComponentStore(@Reference HostInfo info) {
        domainUri = info.getDomain();
    }

    public LogicalCompositeComponent read() throws RecoveryException {
        Composite type = new Composite(null);
        CompositeImplementation impl = new CompositeImplementation();
        impl.setComponentType(type);
        ComponentDefinition<CompositeImplementation> definition =
                new ComponentDefinition<CompositeImplementation>(domainUri.toString());
        definition.setImplementation(impl);
        type.setAutowire(autowire);
        return new LogicalCompositeComponent(domainUri, domainUri, definition, null);
    }

    public void store(LogicalCompositeComponent domain) {
        // no op
    }
}
