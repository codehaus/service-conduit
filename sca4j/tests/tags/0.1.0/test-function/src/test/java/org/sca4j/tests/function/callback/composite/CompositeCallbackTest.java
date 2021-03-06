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
package org.sca4j.tests.function.callback.composite;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * Tests for callbacks with composite-scoped components.
 *
 * @version $Rev: 2751 $ $Date: 2008-02-12 01:14:41 -0800 (Tue, 12 Feb 2008) $
 */
public class CompositeCallbackTest extends TestCase {
    @Reference
    protected ClientService client;

    public void testCompositeCallback() throws Exception {
        CallbackData data = new CallbackData(1);
        client.invoke(data);
        data.getLatch().await();
        assertTrue(data.isCalledBack());
    }

}
