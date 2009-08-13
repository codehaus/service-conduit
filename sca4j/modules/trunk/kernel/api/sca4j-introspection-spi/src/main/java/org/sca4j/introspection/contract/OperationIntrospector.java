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

import java.lang.reflect.Method;

import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ValidationContext;

/**
 * Implementations evaluate the methods of a Java-based interface and populate the operation on the corresponding service contract with relevant
 * metadata.
 *
 * @version $Revision$ $Date$
 */
public interface OperationIntrospector {

    /**
     * Perform the introspection.
     *
     * @param operation the operation to update
     * @param method    the method to evaluate
     * @param context   the validation cotnext to report errors and warnings.
     */
    <T> void introspect(Operation<T> operation, Method method, ValidationContext context);

}
