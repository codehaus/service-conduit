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
package org.sca4j.spi.services.event;

/**
 * Implementations are notified of runtime events after they have subscribed with the {@link EventService} for a
 * particular event type or types.
 *
 * @version $Rev: 148 $ $Date: 2007-06-02 05:10:48 +0100 (Sat, 02 Jun 2007) $
 */
public interface SCA4JEventListener {

    /**
     * Notifies the listener of an event. The listener must not throw an exception as all listeners are notified on the
     * same thread.
     *
     * @param event the event
     */
    void onEvent(SCA4JEvent event);

}
