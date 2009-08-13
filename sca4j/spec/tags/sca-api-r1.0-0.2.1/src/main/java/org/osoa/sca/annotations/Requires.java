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

/**
 * Annotation that allows the attachment of any intent to a Java Class or interface or to members of that
 * class such as methods, fields or constructor parameters.
 * <p/>
 * Intents are specified as XML QNames in the representation defined by
 * {@link javax.xml.namespace.QName#toString() QName#toString()}. Intents may be qualified with one or more
 * suffixes separated by a "." such as:
 * <ul>
 * <li>{http://www.osoa.org/xmlns/sca/1.0}confidentiality</li>
 * <li>{http://www.osoa.org/xmlns/sca/1.0}confidentiality.message</li>
 * </ul>
 * This annotation supports general purpose intents specified as strings.  Users may also define
 * specific intents using the {@link Intent} annotation.
 *
 * @version $Rev: 875 $ $Date: 2007-08-27 17:23:01 +0100 (Mon, 27 Aug 2007) $
 */
@Inherited
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Requires {
    /**
     * Returns the attached intents.
     *
     * @return the attached intents
     */
    String[] value() default "";
}
