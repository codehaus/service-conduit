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

package org.sca4j.fabric.services.expression;

import org.osoa.sca.annotations.Reference;

import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.services.expression.ExpressionEvaluator;

/**
 * @version $Revision$ $Date$
 */
public class HostPropertiesExpressionEvaluator implements ExpressionEvaluator {
    private HostInfo info;

    public HostPropertiesExpressionEvaluator(@Reference HostInfo info) {
        this.info = info;
    }

    public String evaluate(String expression) {
        return info.getProperty(expression, null);
    }
}
