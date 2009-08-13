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
package org.sca4j.scdl.validation;

import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;

/**
 * Validation failure indicating that a component definition does not have an associated implementation.
 *
 * @version $Rev: 4954 $ $Date: 2008-07-06 15:36:35 +0100 (Sun, 06 Jul 2008) $
 */
public class MissingImplementation extends ValidationFailure<ComponentDefinition<? extends Implementation<?>>> {
    public MissingImplementation(ComponentDefinition<? extends Implementation<?>> component) {
        super(component);
    }

    public String getMessage() {
        if (getValidatable() == null) {
            return "Missing implementation for component";
        }
        return "Missing implementation for component " + getValidatable().getName();

    }

}
