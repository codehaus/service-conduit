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
package org.sca4j.scdl;

import java.util.List;

import org.sca4j.host.contribution.ValidationFailure;

/**
 * Context for gathering the results of model validation.
 * <p/>
 * The context allows both errors and warnings to be gathered. Errors indicate problems that will definitely prevent the model object from being
 * activated such as a missing implementation. Warnings indicate issues that are not in themselves fatal but which may result in an activation
 * failure. For example, a composite service may promote a component that it does not contain. This is likely to be an error but would successfully
 * activate if the component was defined by another composite that included this one.
 *
 * @version $Rev: 4336 $ $Date: 2008-05-25 10:06:15 +0100 (Sun, 25 May 2008) $
 */
public interface ValidationContext {

    /**
     * Returns true if the validation has detected any fatal errors.
     *
     * @return true if the validation has detected any fatal errors
     */
    boolean hasErrors();

    /**
     * Returns the list of fatal errors detected during validation.
     *
     * @return the list of fatal errors detected during validation
     */
    List<ValidationFailure> getErrors();

    /**
     * Add a fatal error to the validation results.
     *
     * @param e the fatal error that has been found
     */
    void addError(ValidationFailure e);

    /**
     * Add a collection of fatal errors to the validation results.
     *
     * @param errors the fatal errors that have been found
     */
    void addErrors(List<ValidationFailure> errors);

    /**
     * Returns true if the validation has detected any non-fatal warnings.
     *
     * @return true if the validation has detected any non-fatal warnings
     */
    boolean hasWarnings();

    /**
     * Returns the list of non-fatal warnings detected during validation.
     *
     * @return the list of non-fatal warnings detected during validation
     */
    List<ValidationFailure> getWarnings();

    /**
     * Add a non-fatal warning to the validation results.
     *
     * @param e the non-fatal warning that has been found
     */
    void addWarning(ValidationFailure e);


    /**
     * Add a collection of non-fatal warnings to the validation results.
     *
     * @param warnings the non-fatal warnings that have been found
     */
    void addWarnings(List<ValidationFailure> warnings);

}
