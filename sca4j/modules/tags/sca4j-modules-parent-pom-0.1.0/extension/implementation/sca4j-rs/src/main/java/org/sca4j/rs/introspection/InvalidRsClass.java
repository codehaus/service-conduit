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
package org.sca4j.rs.introspection;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import org.sca4j.host.contribution.ValidationFailure;

/**
 * @version $Rev: 4739 $ $Date: 2008-06-05 19:39:31 +0100 (Thu, 05 Jun 2008) $
 */
public class InvalidRsClass extends ValidationFailure<Class<?>> {

    public InvalidRsClass(Class<?> implClass) {
        super(implClass);
    }

    @Override
    public String getMessage() {
        return String.format("Implementation class %s is not annotated with REST annotation %s or %s ", getValidatable().getName(), Path.class.getName(), Provider.class.getName());
    }
}
