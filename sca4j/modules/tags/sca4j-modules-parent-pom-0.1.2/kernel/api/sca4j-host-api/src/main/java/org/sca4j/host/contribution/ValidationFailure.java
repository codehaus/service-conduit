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
package org.sca4j.host.contribution;

/**
 * Base class for validation failures.
 *
 * @version $Rev: 4336 $ $Date: 2008-05-25 10:06:15 +0100 (Sun, 25 May 2008) $
 */
public abstract class ValidationFailure<T> {
    private final T validatable;

    /**
     * Constructor specifying the validatable object associated with this failure.
     *
     * @param modelObject the model object associated with this failure
     */
    protected ValidationFailure(T modelObject) {
        this.validatable = modelObject;
    }

    /**
     * Returns the object that failed validation.
     *
     * @return the object that failed validation
     */
    public T getValidatable() {
        return validatable;
    }

    public String getMessage() {
        return getClass().getName();
    }
}
