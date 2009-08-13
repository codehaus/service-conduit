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

import javax.jws.WebMethod;

import org.apache.axiom.om.OMElement;

/**
 * @version $Rev: 5466 $ $Date: 2008-09-21 23:15:10 +0100 (Sun, 21 Sep 2008) $
 */
public interface Axis2EchoService {
    /**
     * Operation to demonstrate WSS Username token in action.
     * @param message request message
     * @return response message
     * @see securtity policy and scdl files to see how username token security is enforced.
     */
    @WebMethod(action="echoWs")
    OMElement echoWsUsernameToken(OMElement message);
    
    /**
     * Operation to demonstrate WSS X509 token in action.
     * @param message request message
     * @return response message
     * @see securtity policy and scdl file to see how x509 token security is enforced.
     */   
    OMElement echoWsX509Token(OMElement message);
    
    /**
     * Operation with no security.
     * @param message
     * @return
     */
    OMElement echoNoSecurity(OMElement message);
}
