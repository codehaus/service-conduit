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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInMessageReceiver;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Axis2 to SCA4J proxy - implemented as Axis2 Message Receiver & engaged on operation level.
 * This proxy is to handle IN_ONLY or ROBUST_IN_ONLY web service operations.
 * 
 */
public class InOnlyServiceProxy extends AbstractInMessageReceiver {

    private final InvocationChain invocationChain;

    /**
     * @param invocationChain the invocation chain to invoke
     */
    public InOnlyServiceProxy(InvocationChain invocationChain) {
        this.invocationChain = invocationChain;
    }   
   
    /** 
     * {@inheritDoc}
     */
    @Override
    protected void invokeBusinessLogic(MessageContext messageCtx) throws AxisFault {

        Interceptor head = invocationChain.getHeadInterceptor();
        OMElement bodyContent = ServiceProxyHelper.getInBodyContent(messageCtx);
        Object[] args = bodyContent == null ? null : new Object[]{bodyContent};

        WorkContext workContext = new WorkContext();
        //Attach authenticated Subject to work context
        ServiceProxyHelper.attachSubjectToWorkContext(workContext, messageCtx);

        Message input = new MessageImpl(args, false, workContext);

        Message ret = head.invoke(input);
        
        Object element = ret.getBody();
        if (element instanceof AxisFault) {
            throw (AxisFault) element;
            
        } else if (element instanceof Throwable) {
            throw AxisFault.makeFault((Throwable) element);
            
        } else if (element instanceof OMElement) {
            OMElement webFault =  (OMElement)element;
            AxisFault fault = new AxisFault(webFault.getQName().toString());
            fault.setDetail(webFault);            
            throw fault;
        }        
    }
}