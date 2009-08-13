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
package org.sca4j.tests.function.conversation;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 5388 $ $Date: 2008-09-11 07:37:42 +0100 (Thu, 11 Sep 2008) $
 */
public class ConversationTest extends TestCase {
    @Reference
    public ConversationalService service;

    @Reference
    public ConversationalService subService;

    @Reference
    public ConversationalService compositeService;

//    @Reference
//    public ConversationalService maxAgeService;

    @Reference
    public ConversationalService compositeSubService;

    public void testConversationIdIsInjected() {
        assertNotNull(service.getConversationId());
    }

    public void testStateIsRetained() {
        assertNull(service.getValue());
        service.setValue("Hello");
        assertEquals("Hello", service.getValue());
        assertEquals("Hello", service.getValue());
    }

    public void testEndConversation() {
        service.setValue("Hello");
        assertEquals("Hello", service.end());
        assertNull(service.getValue());
    }

    public void testConversationalityIsInherited() {
        assertNull(subService.getValue());
        subService.setValue("Hello");
        assertEquals("Hello", subService.getValue());
        assertEquals("Hello", subService.getValue());
    }

    public void testCompositeStateIsRetained() {
        assertNull(compositeService.getValue());
        compositeService.setValue("Hello");
        assertEquals("Hello", compositeService.getValue());
        assertEquals("Hello", compositeService.getValue());
    }

    public void testCompositeEndConversation() {
        compositeService.setValue("Hello");
        assertEquals("Hello", compositeService.end());
        assertNull(compositeService.getValue());
    }

    public void testCompositeConversationalityIsInherited() {
        assertNull(compositeSubService.getValue());
        compositeSubService.setValue("Hello");
        assertEquals("Hello", compositeSubService.getValue());
        assertEquals("Hello", compositeSubService.getValue());
    }

//    public void testExpiration() throws Exception{
//        maxAgeService.setValue("Hello");
//        Thread.sleep(4000);
//        maxAgeService.setValue("Hello");
//    }

}
