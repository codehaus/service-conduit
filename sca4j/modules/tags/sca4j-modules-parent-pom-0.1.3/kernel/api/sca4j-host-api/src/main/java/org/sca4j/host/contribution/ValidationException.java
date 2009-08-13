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

import java.util.List;

/**
 * Base class for exceptions indicating a contribution has failed validation.
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public abstract class ValidationException extends ContributionException {
    private static final long serialVersionUID = -9097590343387033730L;

    private final List<ValidationFailure> errors;
    private final List<ValidationFailure> warnings;

    /**
     * Constructor that initializes the initial list of errors and warnings.
     *
     * @param errors   the list of errors
     * @param warnings the list of warnings
     */
    protected ValidationException(List<ValidationFailure> errors, List<ValidationFailure> warnings) {
        super("Validation errors were found");
        this.errors = errors;
        this.warnings = warnings;
    }

    /**
     * Returns a collection of underlying errors associated with this exception.
     *
     * @return the collection of underlying errors
     */
    public List<ValidationFailure> getErrors() {
        return errors;
    }

    /**
     * Returns a collection of underlying warnings associated with this exception.
     *
     * @return the collection of underlying errors
     */
    public List<ValidationFailure> getWarnings() {
        return warnings;
    }

}
