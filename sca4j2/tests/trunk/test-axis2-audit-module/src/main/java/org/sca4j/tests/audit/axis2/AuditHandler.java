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
package org.sca4j.tests.audit.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;

public class AuditHandler extends AbstractHandler {

    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        if(!msgContext.isEngaged("axis2_audit")) {
            return InvocationResponse.CONTINUE;
        }
        System.out.println("<Audit> Flow: " + msgContext.getFLOW() +" Msg: " + msgContext.getEnvelope().toString());
        return InvocationResponse.CONTINUE;
    }

}
