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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.spi.services.event.EventService;
import org.sca4j.spi.services.event.SCA4JEvent;
import org.sca4j.spi.services.event.SCA4JEventListener;

/**
 * Default implementation of the EventService.
 *
 * @version $Rev: 148 $ $Date: 2007-06-02 05:10:48 +0100 (Sat, 02 Jun 2007) $
 */
@EagerInit
public class EventServiceImpl implements EventService {
    private Map<Class<? extends SCA4JEvent>, List<SCA4JEventListener>> cache;

    public EventServiceImpl() {
        cache = new ConcurrentHashMap<Class<? extends SCA4JEvent>, List<SCA4JEventListener>>();
    }

    public <T extends SCA4JEvent> void publish(T event) {
        List<SCA4JEventListener> listeners = cache.get(event.getClass());
        if (listeners == null) {
            return;
        }
        for (SCA4JEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public <T extends SCA4JEvent> void subscribe(Class<T> type, SCA4JEventListener listener) {
        List<SCA4JEventListener> listeners = cache.get(type);
        if (listeners == null) {
            listeners = new ArrayList<SCA4JEventListener>();
            cache.put(type, listeners);
        }
        listeners.add(listener);
    }

    public <T extends SCA4JEvent> void unsubscribe(Class<T> type, SCA4JEventListener listener) {
        List<SCA4JEventListener> listeners = cache.get(type);
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }
}
