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

package org.sca4j.tests.function.wiring;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.HelloService;

/**
 * @version $Rev: 1962 $ $Date: 2007-11-12 18:51:34 +0000 (Mon, 12 Nov 2007) $
 */
public class SingleReferenceTest extends TestCase {

    private HelloService service;

    @Reference
    public void setTestService(HelloService service) {
        this.service = service;
    }

    /**
     * Tests reference promotion case where promotion URI only includes the component name and not the reference. SCA
     * allows defaulting if the component has only one reference.
     */
    public void testPromotedDefaultReference() throws Exception {
        assertNotNull(service);
    }

}
