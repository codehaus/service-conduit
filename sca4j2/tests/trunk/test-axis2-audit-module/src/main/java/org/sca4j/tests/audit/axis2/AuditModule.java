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
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

public class AuditModule implements Module {

    public void applyPolicy(Policy arg0, AxisDescription arg1) throws AxisFault {

    }

    public boolean canSupportAssertion(Assertion arg0) {
        return false;
    }

    public void engageNotify(AxisDescription arg0) throws AxisFault {
    }

    public void init(ConfigurationContext arg0, AxisModule arg1) throws AxisFault {
    }

    public void shutdown(ConfigurationContext arg0) throws AxisFault {
    }

}
