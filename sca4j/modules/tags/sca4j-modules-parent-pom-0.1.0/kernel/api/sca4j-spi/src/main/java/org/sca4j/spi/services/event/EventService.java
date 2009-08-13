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
 * The runtime event service. {@link SCA4JEventListener}s subscribe with this service to receive notification of
 * various runtime events.
 *
 * @version $Rev: 148 $ $Date: 2007-06-02 05:10:48 +0100 (Sat, 02 Jun 2007) $
 */
public interface EventService {

    /**
     * Publishes a runtime event. EventListeners subscribed to the event will be notified.
     *
     * @param event the event
     */
    <T extends SCA4JEvent> void publish(T event);

    /**
     * Subscribes the listener to receive notification when events of the specified type are published.
     *
     * @param type     the event type to receive notifications for
     * @param listener the listener to subscribe
     */
    <T extends SCA4JEvent> void subscribe(Class<T> type, SCA4JEventListener listener);

    /**
     * Unsubscribes the listener from receiving notifications when events of the specified type are published.
     *
     * @param type     the event type to unsibscribe from
     * @param listener the listener to unsubscribe
     */
    <T extends SCA4JEvent> void unsubscribe(Class<T> type, SCA4JEventListener listener);


}
