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
package org.osoa.sca.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

import static org.osoa.sca.Constants.SCA_PREFIX;

/**
 * Annotation denoting the intent that service operations require confidentiality.
 * <p/>
 * Applied to the injection site (field, method or constructor parameter) for a reference,
 * it indicates that all invocations through that reference require confidentiality.
 * <p/>
 * Applied to a interface method on a service contract, it indicates that all invocations
 * of that service operation require confidentiality; applied to the type of a service contract,
 * it indicates that all service operations on that interface require confidentiality.
 * <p/>
 * Applied to a method on an implementation class, it indicates that all invocations that
 * are dispatched to that implementation method (through any service) require confidentiality.
 * Applied to a interface implemented by an implementation class, it indicates that all
 * invocations that are dispatched to the implementation method for that interface operation
 * require confidentiality.
 * <p/>
 * Applied to an implementation class, it indicates that all invocations of that implementation
 * and that all invocations made by that implementation require confidentiality.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(Confidentiality.CONFIDENTIALITY)
public @interface Confidentiality {
    String CONFIDENTIALITY = SCA_PREFIX + "confidentiality";
    String CONFIDENTIALITY_MESSAGE = CONFIDENTIALITY + ".message";
    String CONFIDENTIALITY_TRANSPORT = CONFIDENTIALITY + ".transport";

    /**
     * List of confidentiality qualifiers (such as "message" or "transport").
     *
     * @return confidentiality qualifiers
     */
    @Qualifier
    String[] value() default "";
}
