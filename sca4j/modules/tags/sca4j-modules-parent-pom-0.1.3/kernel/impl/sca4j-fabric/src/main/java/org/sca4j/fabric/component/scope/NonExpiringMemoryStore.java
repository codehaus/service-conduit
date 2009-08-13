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
package org.sca4j.fabric.component.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.InstanceWrapperStore;
import org.sca4j.spi.component.StoreException;

/**
 * A simple store that just retains instances in memory without expiration. Basically, a HashMap.
 *
 * @version $Rev: 3515 $ $Date: 2008-03-31 04:27:37 +0100 (Mon, 31 Mar 2008) $
 */
public class NonExpiringMemoryStore<KEY> implements InstanceWrapperStore<KEY> {
    private final Map<KEY, Map<AtomicComponent<?>, InstanceWrapper<?>>> contexts =
            new ConcurrentHashMap<KEY, Map<AtomicComponent<?>, InstanceWrapper<?>>>();

    public void startContext(KEY contextId) throws StoreException {
        contexts.put(contextId, new ConcurrentHashMap<AtomicComponent<?>, InstanceWrapper<?>>());
    }

    public void stopContext(KEY contextId) throws StoreException {
        contexts.remove(contextId);
    }

    @SuppressWarnings("unchecked")
    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, KEY contextId) throws StoreException {
        Map<AtomicComponent<?>, InstanceWrapper<?>> context = contexts.get(contextId);
        if (context == null) {
            return null;
        }
        return (InstanceWrapper<T>) context.get(component);
    }

    public <T> void putWrapper(AtomicComponent<T> component, KEY contextId, InstanceWrapper<T> wrapper)
            throws StoreException {
        Map<AtomicComponent<?>, InstanceWrapper<?>> context = contexts.get(contextId);
        assert context != null;
        context.put(component, wrapper);
    }
}
