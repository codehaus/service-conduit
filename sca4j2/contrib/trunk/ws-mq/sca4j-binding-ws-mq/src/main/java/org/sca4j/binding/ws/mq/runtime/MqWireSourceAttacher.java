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

package org.sca4j.binding.ws.mq.runtime;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

public class MqWireSourceAttacher implements SourceWireAttacher {


	/** 
	 * 
	 */
	public void attachToSource(PhysicalWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition wireTargetDefinition, Wire wire) throws WiringException {
         throw new AssertionError("Not Implemented");
	}

	/**
	 * 
	 */
	public void detachFromSource(PhysicalWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition wireTargetDefinition, Wire wire) throws WiringException {
         throw new AssertionError("Not Implemented");
	}
	
	/* 
	 * 
	 */
	public void attachObjectFactory(PhysicalWireSourceDefinition sourceDefinition, ObjectFactory factory, PhysicalWireTargetDefinition wireTargetDefinition) throws WiringException {
		throw new AssertionError("Not Supported");
	}


}
