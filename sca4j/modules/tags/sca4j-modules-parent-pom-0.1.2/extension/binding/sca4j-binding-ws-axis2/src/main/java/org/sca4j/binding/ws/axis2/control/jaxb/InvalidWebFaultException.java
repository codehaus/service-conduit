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
package org.sca4j.binding.ws.axis2.control.jaxb;

import org.sca4j.spi.generator.GenerationException;

/**
 * @version $Rev: 4070 $ $Date: 2008-04-29 23:08:00 +0100 (Tue, 29 Apr 2008) $
 */
public class InvalidWebFaultException extends GenerationException {

    public InvalidWebFaultException(String identifier) {
        super(identifier);
    }

    public String getMessage() {
        return "Exception class does not have @WebFault annotation required by JAX-WS: " + getIdentifier();
    }
}
