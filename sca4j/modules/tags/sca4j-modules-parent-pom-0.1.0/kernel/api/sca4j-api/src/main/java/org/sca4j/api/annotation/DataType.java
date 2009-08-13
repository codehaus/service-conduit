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
package org.sca4j.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Used to demarcate expected data types for an operation
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface DataType {

    /**
     * Returns the unique name of the data binding
     * @return the unique name of the data binding
     */
    String name();

    /**
     * Returns the logical data type
     * @return the logical data type
     */
    Class logicalType() default Object.class;

    /**
     * Returns the physical data type
     * @return the physical data type
     */
    Class physicalType() default Object.class;

    /**
     * Returns an array of extensibility elements
     * @return an array of extensibility elements
     */
    DataContext[] context() default {};

}
