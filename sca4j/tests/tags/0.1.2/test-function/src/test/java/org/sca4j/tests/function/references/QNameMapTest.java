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
package org.sca4j.tests.function.references;

import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.IdentityService;

/**
 * @version $Rev: 1392 $ $Date: 2007-09-26 21:43:01 +0100 (Wed, 26 Sep 2007) $
 */
public class QNameMapTest extends TestCase {

    @Reference
    public Map<QName, IdentityService> field;

    public void testField() {
        checkMap(field);
    }

    private void checkMap(Map<QName, IdentityService> map) {
        QName name = new QName("file://./foo", "test");
        assertEquals(1, map.size());
        assertEquals("test.test", map.get(name).getIdentity());
    }
}
