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
package org.sca4j.proxy.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Responsible for dispatching to a callback service from multi-threaded component instances such as composite scope components. Since callback
 * proxies for multi-threaded components may dispatch to multiple callback services, this implementation must determine the correct target service
 * based on the current CallFrame. For example, if clients A and A' implementing the same callback interface C invoke B, the callback proxy
 * representing C must correctly dispatch back to A and A'. This is done by recording the callback URI in the current CallFrame as the forward invoke
 * is made.
 *
 * @version $Rev: 3150 $ $Date: 2008-03-21 14:12:51 -0700 (Fri, 21 Mar 2008) $
 */
public class MultiThreadedCallbackInvocationHandler<T> extends AbstractCallbackInvocationHandler<T> {
    private Map<String, Map<Method, InvocationChain>> mappings;

    /**
     * Constructor. In multi-threaded instances such as composite scoped components, multiple forward invocations may be received simultaneously. As a
     * result, since callback proxies stored in instance variables may represent multiple clients, they must map the correct one for the request being
     * processed on the current thread. The mappings parameter keys a callback URI representing the client to the set of invocation chains for the
     * callback service.
     *
     * @param interfaze the callback service interface implemented by the proxy
     * @param mappings  the callback URI to invocation chain mappings
     */
    public MultiThreadedCallbackInvocationHandler(Class<T> interfaze, Map<String, Map<Method, InvocationChain>> mappings) {
        super(interfaze);
        this.mappings = mappings;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        CallFrame frame = workContext.peekCallFrame();
        String callbackUri = frame.getCallbackUri();
        Map<Method, InvocationChain> chains = mappings.get(callbackUri);
        // find the invocation chain for the invoked operation
        InvocationChain chain = chains.get(method);
        // find the invocation chain for the invoked operation
        if (chain == null) {
            return handleProxyMethod(method);
        }
        return super.invoke(chain, args, workContext);
    }

}
