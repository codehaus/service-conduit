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

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutMessageReceiver;
import org.sca4j.binding.ws.axis2.runtime.jaxb.FaultData;
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
        ServiceProxyHelper.attachSubjectToWorkContext(workContext, inMessage);

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
            } else if (element instanceof FaultData) {
                FaultData faultData = (FaultData) element;
                throw faultData.asAxisFault();
            }
        } else {
            OMElement resObject = (OMElement) ret.getBody();
            body.addChild(resObject);
        }

        outMessage.setEnvelope(envelope);

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
