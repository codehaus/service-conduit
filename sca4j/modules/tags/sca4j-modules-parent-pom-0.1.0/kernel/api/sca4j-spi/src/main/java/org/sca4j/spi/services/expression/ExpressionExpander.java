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

package org.sca4j.spi.services.expression;

/**
 * Expands strings containing expressions delimited by '${' and '}' by delegating to an ExpressionEvaluator. Expression values may be sourced using a
 * variety of methods, including system and environment properties. Expression expansion is used to subsitute configuration values specified in a
 * composite file with runtime values. For example:
 * <pre>
 * <binding.ws uri="http://${myservice.endpoint}/>
 * </pre>
 *
 * @version $Revision$ $Date$
 */
public interface ExpressionExpander {

    /**
     * A string value containing an expression or expressions to expand. The string may contain multiple expressions.
     *
     * @param value the value to expand
     * @return the expanded string
     * @throws ExpressionExpansionException if an error occurs evaluating an expression
     */
    String expand(String value) throws ExpressionExpansionException;

}
