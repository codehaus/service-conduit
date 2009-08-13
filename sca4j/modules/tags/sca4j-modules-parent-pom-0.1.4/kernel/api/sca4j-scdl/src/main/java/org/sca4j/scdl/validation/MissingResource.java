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

/**
 * Denotes a missing resource such as a class file.
 *
 * @version $Revision$ $Date$
 */
public class MissingResource extends ValidationFailure<String> {
    private String description;

    public MissingResource(String description, String name) {
        super(name);
        this.description = description;
    }

    public String getMessage() {
        return description + ": " + getValidatable();
    }
}
