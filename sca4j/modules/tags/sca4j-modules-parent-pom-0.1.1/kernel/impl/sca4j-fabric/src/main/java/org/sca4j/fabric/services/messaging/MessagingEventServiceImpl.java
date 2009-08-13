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
package org.sca4j.fabric.services.messaging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.spi.services.messaging.MessagingEventService;
import org.sca4j.spi.services.messaging.RequestListener;

/**
 * MessagingEventService implementation.
 *
 * @version $Rev: 2364 $ $Date: 2007-12-28 04:08:29 +0000 (Fri, 28 Dec 2007) $
 */
public class MessagingEventServiceImpl implements MessagingEventService {
    private Map<QName, RequestListener> cache = new ConcurrentHashMap<QName, RequestListener>();

    public void registerRequestListener(QName messageType, RequestListener listener) {
        cache.put(messageType, listener);
    }

    public void unRegisterRequestListener(QName messageType) {
        cache.remove(messageType);
    }

    public void publish(QName messageType, XMLStreamReader content) {
        RequestListener listener = cache.get(messageType);
        if (listener == null) {
            // ignore th message
            return;
        }
        listener.onRequest(content);
    }
}
