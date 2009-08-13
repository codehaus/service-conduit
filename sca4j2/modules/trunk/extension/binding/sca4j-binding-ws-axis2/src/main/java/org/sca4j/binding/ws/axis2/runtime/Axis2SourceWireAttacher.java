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
package org.sca4j.binding.ws.axis2.runtime;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.binding.ws.axis2.runtime.Axis2ServiceProvisioner;
import org.sca4j.binding.ws.axis2.provision.Axis2WireSourceDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Revision$ $Date$
 *          <p/>
 *          TODO Add support for WSDL contract
 */
@EagerInit
public class Axis2SourceWireAttacher implements SourceWireAttacher<Axis2WireSourceDefinition> {

    private final Axis2ServiceProvisioner serviceProvisioner;

    /**
     * @param serviceProvisioner axis service provisioner
     */
    public Axis2SourceWireAttacher(@Reference Axis2ServiceProvisioner serviceProvisioner) {
        this.serviceProvisioner = serviceProvisioner;
    }

    public void attachToSource(Axis2WireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        serviceProvisioner.provision(source, wire);
    }

    public void detachFromSource(Axis2WireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new AssertionError();        
    }

    public void attachObjectFactory(Axis2WireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition) throws WiringException {
        throw new AssertionError();
    }
}
