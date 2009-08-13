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
package org.sca4j.pojo.reflection;

import org.sca4j.spi.ObjectCreationException;

/**
 * @version $Rev: 2973 $ $Date: 2008-02-29 23:06:57 +0000 (Fri, 29 Feb 2008) $
 */
public class IncompatibleArgumentException extends ObjectCreationException {
    private static final long serialVersionUID = 7837357233260840672L;
    private final int param;
    private final String type;

    public IncompatibleArgumentException(String identifier, int param, String type) {
        super(null, identifier);
        this.param = param;
        this.type = type;
    }

    public String getMessage() {
        return "Unable to assign parameter of type " + type + " to parameter " + param + "of " + getIdentifier();
    }
}
