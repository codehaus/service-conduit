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
package org.sca4j.binding.test;

import java.net.URI;

import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 4853 $ $Date: 2008-06-20 11:51:52 +0100 (Fri, 20 Jun 2008) $
 */
public class TestBindingSourceWireAttacher implements SourceWireAttacher<TestBindingSourceDefinition> {
    private final BindingChannel channel;

    public TestBindingSourceWireAttacher(@Reference BindingChannel channel) {
        this.channel = channel;
    }

    public void attachToSource(TestBindingSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        // register the wire to the bound service so it can be invoked through the channel from a bound reference
        URI callbackUri = target.getCallbackUri();
        channel.registerDestinationWire(source.getUri(), wire, callbackUri);
    }

    public void detachFromSource(TestBindingSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new AssertionError();
    }

    public void attachObjectFactory(TestBindingSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition) throws WiringException {
        throw new AssertionError();
    }
}
