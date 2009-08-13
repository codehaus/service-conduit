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
package org.sca4j.introspection.java;

import org.sca4j.introspection.IntrospectionException;

/**
 * @version $Rev: 4278 $ $Date: 2008-05-20 08:22:30 +0100 (Tue, 20 May 2008) $
 */
public class ImplementationNotFoundException extends IntrospectionException {
    private static final long serialVersionUID = -5872848682083357587L;

    public ImplementationNotFoundException(String identifier) {
        super(identifier);
    }

    public ImplementationNotFoundException(String identifier, Throwable cause) {
        super(identifier, cause);
    }

    public String getMessage() {
        return "Unable to load implementation class: " + getIdentifier();
    }
}
