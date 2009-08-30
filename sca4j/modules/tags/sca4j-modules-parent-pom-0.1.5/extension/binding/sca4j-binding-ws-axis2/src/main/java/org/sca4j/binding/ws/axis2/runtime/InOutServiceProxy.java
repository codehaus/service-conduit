/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.binding.ws.axis2.runtime;

import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.security.auth.Subject;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutMessageReceiver;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.apache.ws.security.util.WSSecurityUtil;

import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Axis2 to SCA4J proxy - implemented as Axis2 Message Receiver & engaged on operation level.
 * This proxy is to handle IN_OUT web service operations.
 * 
 */
public class InOutServiceProxy extends AbstractInOutMessageReceiver {

    private final InvocationChain invocationChain;

    /**
     * @param invocationChain the invocation chain to invoke
     */
    public InOutServiceProxy(InvocationChain invocationChain) {
        this.invocationChain = invocationChain;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void invokeBusinessLogic(MessageContext inMessage, MessageContext outMessage) throws AxisFault {

        Interceptor head = invocationChain.getHeadInterceptor();
        OMElement bodyContent = getInBodyContent(inMessage);
        Object[] args = bodyContent == null ? null : new Object[]{bodyContent};

        WorkContext workContext = new WorkContext();
        //Attach authenticated Subject to work context
        attachSubjectToWorkContext(workContext, inMessage);

        Message input = new MessageImpl(args, false, workContext);

        Message ret = head.invoke(input);

        SOAPFactory fac = getSOAPFactory(inMessage);
        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        SOAPBody body = envelope.getBody();

        if (ret.isFault()) {
            Object element = ret.getBody();
            if (element instanceof AxisFault) {
                throw (AxisFault) element;
            } else if (element instanceof Throwable) {
                throw AxisFault.makeFault((Throwable) element);
            }
        } else {
            OMElement resObject = (OMElement) ret.getBody();
            body.addChild(resObject);
        }

        outMessage.setEnvelope(envelope);

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
    private void attachSubjectToWorkContext(WorkContext workContext, MessageContext inMessage) {
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

    /*
     * Gets the body content of the incoming message.
     */
    private OMElement getInBodyContent(MessageContext inMessage) {

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
