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
package org.sca4j.fabric.async;

import junit.framework.TestCase;

/**
 * @version $Rev: 3126 $ $Date: 2008-03-16 20:16:42 +0000 (Sun, 16 Mar 2008) $
 */
public class NonBlockingInterceptorBuilderTestCase extends TestCase {

    public void testBuild() throws Exception {
        NonBlockingInterceptorBuilder builder = new NonBlockingInterceptorBuilder(null);
        NonBlockingInterceptorDefinition definition = new NonBlockingInterceptorDefinition();
        assertNotNull(builder.build(definition));
    }
}
