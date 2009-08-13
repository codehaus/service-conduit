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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * F3 specific persistence unit annotation that suppports CDI for JPA entity manager factories.
 * 
 * Standard JPA annotation can be applied on constructor arguments.
 * 
 * @version $Revision$ $Date$
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistenceUnit {

    /**
     * @return The name by which the entity manager factory is to be accessed in 
     * the environment referencing context, and is not needed when dependency 
     * injection is used.
     */
    public abstract java.lang.String name() default "";

    /**
     * @return The name of the persistence unit as defined in the persistence.xml file.
     */
    public abstract java.lang.String unitName() default "";

}
