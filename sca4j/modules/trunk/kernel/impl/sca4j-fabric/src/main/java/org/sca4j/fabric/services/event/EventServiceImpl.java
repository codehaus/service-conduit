/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
