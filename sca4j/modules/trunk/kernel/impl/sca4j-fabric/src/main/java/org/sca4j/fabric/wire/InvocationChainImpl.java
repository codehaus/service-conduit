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
package org.sca4j.fabric.wire;

import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Default implementation of an invocation chain
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
public class InvocationChainImpl implements InvocationChain {
    protected PhysicalOperationDefinition physicalOperation;
    protected Interceptor interceptorChainHead;
    protected Interceptor interceptorChainTail;

    public InvocationChainImpl(PhysicalOperationDefinition operation) {
        this.physicalOperation = operation;
    }

    public PhysicalOperationDefinition getPhysicalOperation() {
        return physicalOperation;
    }

    public void addInterceptor(Interceptor interceptor) {
        if (interceptorChainHead == null) {
            interceptorChainHead = interceptor;
        } else {
            interceptorChainTail.setNext(interceptor);
        }
        interceptorChainTail = interceptor;
    }

    public void addInterceptor(int index, Interceptor interceptor) {
        int i = 0;
        Interceptor next = interceptorChainHead;
        Interceptor prev = null;
        while (next != null && i < index) {
            prev = next;
            next = next.getNext();
            i++;
        }
        if (i == index) {
            if (prev != null) {
                prev.setNext(interceptor);
            } else {
                interceptorChainHead = interceptor;
            }
            interceptor.setNext(next);
            if (next == null) {
                interceptorChainTail = interceptor;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public Interceptor getHeadInterceptor() {
        return interceptorChainHead;
    }

    public Interceptor getTailInterceptor() {
        return interceptorChainTail;
    }

}
