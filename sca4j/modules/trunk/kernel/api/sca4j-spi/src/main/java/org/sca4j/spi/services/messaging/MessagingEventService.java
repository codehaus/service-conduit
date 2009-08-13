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
package org.sca4j.spi.services.messaging;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * @version $Rev: 2364 $ $Date: 2007-12-28 04:08:29 +0000 (Fri, 28 Dec 2007) $
 */
public interface MessagingEventService {

    /**
     * Registers a request listener for async messages. Request listeners handle unslolicited async messages sent by
     * recipients.
     *
     * @param messageType Message type that can be handled by the listener.
     * @param listener    Recipient of the async message.
     */
    void registerRequestListener(QName messageType, RequestListener listener);

    /**
     * Un registers a request listener for async messages.
     *
     * @param messageType Message type that can be handled by the listener.
     */
    void unRegisterRequestListener(QName messageType);

    /**
     * Dispatches an event to a registered listener for the message type. If no listener is found, the message is
     * ignored.
     *
     * @param messageType the message type
     * @param content     the message body
     */
    void publish(QName messageType, XMLStreamReader content);

}
