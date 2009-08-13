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
package org.sca4j.tests.function.binding;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.HelloService;

/**
 * Verifies service and reference promotion.
 *
 * @version $Rev: 1433 $ $Date: 2007-10-01 23:58:55 +0100 (Mon, 01 Oct 2007) $
 */
public class PromotionTest extends TestCase {
    private HelloService helloService;

    @Reference
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

    public void testReferenceIsBound() {
        assertEquals("hello", helloService.send("hello"));
    }
}
