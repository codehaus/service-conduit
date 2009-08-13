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
package org.sca4j.binding.ws.axis2.runtime;

import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.security.auth.Subject;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.context.MessageContext;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.apache.ws.security.util.WSSecurityUtil;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Helper class to perform common ServiceProxy operations 
 */
public class ServiceProxyHelper {

    private ServiceProxyHelper() {
        super();
    }

    /**
     * Attaches the Security principal found after axis2/wss4j security processing to work context.
     *
     * @param workContext f3 work context
     * @param inMessage   In coming axis2 message context
     * @see org.apache.ws.security.processor.SignatureProcessor
     * @see org.apache.rampart.handler.RampartReceiver
     */
    @SuppressWarnings("unchecked")
    public static void attachSubjectToWorkContext(WorkContext workContext, MessageContext inMessage) {
        Vector<WSHandlerResult> wsHandlerResults = (Vector<WSHandlerResult>) inMessage.getProperty(WSHandlerConstants.RECV_RESULTS);

        // Iterate over principals
        if ((wsHandlerResults != null) && (wsHandlerResults.size() > 0)) {
            HashSet<Principal> principals = new HashSet<Principal>();

            for (WSHandlerResult wsHandlerResult : wsHandlerResults) {//Iterate through all wsHandler results to find Principals
                Principal foundPrincipal = null;
                WSSecurityEngineResult signResult = WSSecurityUtil.fetchActionResult(wsHandlerResult.getResults(), WSConstants.UT);
                if (signResult == null) {
                    signResult = WSSecurityUtil.fetchActionResult(wsHandlerResult.getResults(), WSConstants.SIGN);
                }

                if (signResult != null) {
                    foundPrincipal = (Principal) signResult.get(WSSecurityEngineResult.TAG_PRINCIPAL);
                }

                //Create Subject with principal found
                if (foundPrincipal != null) {
                    principals.add(foundPrincipal);
                }
            }

            if (principals.size() > 0) {// If we have found principals then set newly created Subject on work context
                workContext.setSubject(new Subject(false, principals, new HashSet<Principal>(), new HashSet<Principal>()));
            }
        }
    }


    /**
     * Gets the body content of the incoming message.
     * @param inMessage Message context associated with InComing message
     * @return OMElement representing SOAP body element
     */
    public static OMElement getInBodyContent(MessageContext inMessage) {

        SOAPEnvelope envelope = inMessage.getEnvelope();

        OMElement child = null;
        Iterator<?> children = envelope.getChildElements();
        while (children.hasNext()) {
            child = (OMElement) children.next();
            if ("Body".equals(child.getLocalName())) {
                break;
            }
        }
        if (child != null) {
            return child.getFirstElement();
        }
        return null;

    }
}
