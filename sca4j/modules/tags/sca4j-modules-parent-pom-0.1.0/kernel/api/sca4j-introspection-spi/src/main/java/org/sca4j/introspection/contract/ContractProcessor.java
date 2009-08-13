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
package org.sca4j.introspection.contract;

import java.lang.reflect.Type;

import org.sca4j.introspection.TypeMapping;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

/**
 * Interface for processors that can construct a ServiceContract from a Java type.
 *
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public interface ContractProcessor {
    /**
     * Introspect a Java Type (e.g. a Class) and return the ServiceContract. If validation errors or warnings are encountered, they will be reported
     * in the ValidationContext.
     *
     * @param typeMapping the type mapping for the interface
     * @param type        the Java Type to introspect
     * @param context     the validation context for reporting errors and warnings
     * @return the ServiceContract corresponding to the interface type
     */
    ServiceContract<Type> introspect(TypeMapping typeMapping, Type type, ValidationContext context);
}
