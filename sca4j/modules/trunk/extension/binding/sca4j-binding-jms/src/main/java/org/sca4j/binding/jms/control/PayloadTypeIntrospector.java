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
package org.sca4j.binding.jms.control;

import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.scdl.Operation;

/**
 * Introspects an operation's in parameters to determine the payload type.
 * 
 * @version $Revision$ $Date$
 */
public interface PayloadTypeIntrospector {

    /**
     * Performs the introspection.
     * 
     * @param operation the operation to introspect
     * @return the JMS message type
     * @throws JmsGenerationException if an error occurs introspecting the
     *             operation
     */
    <T> PayloadType introspect(Operation<T> operation) throws JmsGenerationException;

}
