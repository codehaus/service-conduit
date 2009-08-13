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
package org.sca4j.jpa.runtime;

import org.sca4j.host.SCA4JException;

/**
 * Denotes an exception creating and associating an EntityManager with an execution context.
 *
 * @version $Revision$ $Date$
 */
public class EntityManagerCreationException extends SCA4JException {
    private static final long serialVersionUID = 6562347332589851544L;

    public EntityManagerCreationException(String message) {
        super(message);
    }

    public EntityManagerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityManagerCreationException(Throwable cause) {
        super(cause);
    }
}
