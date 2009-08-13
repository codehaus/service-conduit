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
package org.sca4j.junit.runtime;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.junit.provision.JUnitWireSourceDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

@EagerInit
public class JunitSourceWireAttacher implements SourceWireAttacher<JUnitWireSourceDefinition> {

	private WireHolder holder;

    public JunitSourceWireAttacher(@Reference WireHolder holder) {
        this.holder = holder;
    }

    public void attachToSource(JUnitWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        final String testName = source.getTestName();
        holder.put(testName, wire);
    }

    public void attachObjectFactory(JUnitWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target)
            throws WiringException {
        throw new UnsupportedOperationException();
    }

    public void detachFromSource(JUnitWireSourceDefinition source, PhysicalWireTargetDefinition target) throws WiringException {
        throw new UnsupportedOperationException();
    }

    public void detachObjectFactory(JUnitWireSourceDefinition source, PhysicalWireTargetDefinition target) throws WiringException {
        throw new UnsupportedOperationException();
    }

	public void detachFromSource(JUnitWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
		 throw new UnsupportedOperationException();
	}

}
