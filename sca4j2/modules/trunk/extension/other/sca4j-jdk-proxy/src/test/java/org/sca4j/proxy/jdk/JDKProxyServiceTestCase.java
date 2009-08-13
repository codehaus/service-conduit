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
import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import org.osoa.sca.ServiceReference;

import org.sca4j.spi.wire.InvocationChain;

/**
 * @version $Rev: 2883 $ $Date: 2008-02-24 11:05:35 -0800 (Sun, 24 Feb 2008) $
 */
public class JDKProxyServiceTestCase extends TestCase {
    private JDKProxyService proxyService;

    public void testCastProxyToServiceReference() {
        Map<Method, InvocationChain> mapping = Collections.emptyMap();
        JDKInvocationHandler<Foo> handler = new JDKInvocationHandler<Foo>(Foo.class, null, mapping);
        Foo proxy = handler.getService();
        ServiceReference<Foo> ref = proxyService.cast(proxy);
        assertSame(handler, ref);
    }

    protected void setUp() throws Exception {
        super.setUp();
        proxyService = new JDKProxyService();
    }

    public interface Foo {
    }
}
