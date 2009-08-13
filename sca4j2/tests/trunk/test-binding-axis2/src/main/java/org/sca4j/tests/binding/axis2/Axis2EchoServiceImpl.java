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
package org.sca4j.tests.binding.axis2;

import org.apache.axiom.om.OMElement;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev: 2688 $ $Date: 2008-02-03 16:27:06 +0000 (Sun, 03 Feb 2008) $
 */
@Scope("COMPOSITE")
public class Axis2EchoServiceImpl implements Axis2EchoService {

    public OMElement echoWsUsernameToken(OMElement message) {
        return message;
    }
    
    public OMElement echoWsX509Token(OMElement message) {
        return message;
    }

    public OMElement echoNoSecurity(OMElement message) {
        return message;
    }

}
