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
package org.sca4j.binding.test;

import java.net.URI;

import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.invocation.Message;

/**
 * @version $Rev: 3021 $ $Date: 2008-03-04 03:28:04 +0000 (Tue, 04 Mar 2008) $
 */
public class TestBindingInterceptor implements Interceptor {
    private BindingChannel channel;
    private URI destination;
    private String operation;

    public TestBindingInterceptor(BindingChannel channel, URI destination, String operation) {
        this.channel = channel;
        this.destination = destination;
        this.operation = operation;
    }

    public Message invoke(Message msg) {
        return channel.send(destination, operation, msg);
    }

    public void setNext(Interceptor next) {
        throw new UnsupportedOperationException("Interceptor must be the last in the chain");
    }

    public Interceptor getNext() {
        return null;
    }
}
