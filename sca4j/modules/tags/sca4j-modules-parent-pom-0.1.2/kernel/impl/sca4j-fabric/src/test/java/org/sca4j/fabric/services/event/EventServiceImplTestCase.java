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
package org.sca4j.fabric.services.event;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.spi.services.event.EventService;
import org.sca4j.spi.services.event.SCA4JEvent;
import org.sca4j.spi.services.event.SCA4JEventListener;

/**
 * @version $Rev: 148 $ $Date: 2007-06-02 05:10:48 +0100 (Sat, 02 Jun 2007) $
 */
public class EventServiceImplTestCase extends TestCase {
    private EventService service;
    private SCA4JEventListener listener;

    public void testSubscribeUnsubscribe() throws Exception {
        service.subscribe(MockEvent.class, listener);
        service.publish(new MockEvent());
        service.unsubscribe(MockEvent.class, listener);
        service.publish(new MockEvent());
        EasyMock.verify(listener);
    }


    @SuppressWarnings({"unchecked"})
    protected void setUp() throws Exception {
        super.setUp();
        service = new EventServiceImpl();
        listener = EasyMock.createStrictMock(SCA4JEventListener.class);
        listener.onEvent(EasyMock.isA(MockEvent.class));
        EasyMock.replay(listener);
    }

    private class MockEvent implements SCA4JEvent {

    }
}
