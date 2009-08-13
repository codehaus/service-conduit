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
package org.sca4j.introspection.impl.annotation;

import org.sca4j.host.contribution.ValidationFailure;

/**
 * Denotes an invalid accessor value for a field or method annotated with @Resource, @Reference, or @Property.
 *
 * @version $Revision$ $Date$
 */
public class InvalidAccessor extends ValidationFailure<Class<?>> {
    private String message;

    public InvalidAccessor(String message, Class<?> implClass) {
        super(implClass);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
