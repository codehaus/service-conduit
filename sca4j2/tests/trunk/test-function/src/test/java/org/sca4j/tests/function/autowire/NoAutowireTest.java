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
package org.sca4j.tests.function.autowire;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.Target;

/**
 * @version $Rev: 1897 $ $Date: 2007-11-09 01:18:20 +0000 (Fri, 09 Nov 2007) $
 */
public class NoAutowireTest extends TestCase {
    private Target service;

    @Reference(required = false)
    public void setTarget(Target service) {
        this.service = service;
    }

    public void testNotWired() {
        assertNull(service);
    }

}
