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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osoa.sca.Conversation;
import org.osoa.sca.ConversationEndedException;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.ConversationExpirationCallback;
import org.sca4j.spi.component.ExpirationPolicy;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.InstanceWrapperStore;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.ConversationContext;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Scope container for the standard CONVERSATIONAL scope.
 *
 * @version $Rev: 4786 $ $Date: 2008-06-08 14:37:24 +0100 (Sun, 08 Jun 2008) $
 */
@Service(ScopeContainer.class)
@EagerInit
public class ConversationalScopeContainer extends StatefulScopeContainer<Conversation> {
    private final ConcurrentHashMap<Conversation, ExpirationPolicy> expirationPolicies;
    private final ConcurrentHashMap<Conversation, List<ConversationExpirationCallback>> expirationCallbacks;
    private ScheduledExecutorService executor;
    // TODO this should be part of the system configuration
    private long delay = 600;  // reap every 600 seconds

    public ConversationalScopeContainer(@Monitor ScopeContainerMonitor monitor,
                                        @Reference(name = "store")InstanceWrapperStore<Conversation> store) {
        super(Scope.CONVERSATION, monitor, store);
        expirationPolicies = new ConcurrentHashMap<Conversation, ExpirationPolicy>();
        expirationCallbacks = new ConcurrentHashMap<Conversation, List<ConversationExpirationCallback>>();
    }

    /**
     * Optional property to set the delay for executing the reaper to clear expired conversation contexts
     *
     * @param delay the delay in seconds
     */
    @Property
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Init
    public void start() {
        super.start();
        executor = Executors.newSingleThreadScheduledExecutor();
        Runnable reaper = new Reaper();
        executor.scheduleWithFixedDelay(reaper, delay, delay, TimeUnit.SECONDS);
    }

    @Destroy
    public void stop() {
        executor.shutdownNow();
        super.stop();
    }

    public void registerCallback(Conversation conversation, ConversationExpirationCallback callback) {
        List<ConversationExpirationCallback> callbacks = expirationCallbacks.get(conversation);
        if (callbacks == null) {
            callbacks = new ArrayList<ConversationExpirationCallback>();
            expirationCallbacks.put(conversation, callbacks);
        }
        synchronized (callbacks) {
            callbacks.add(callback);
        }
    }

    public void startContext(WorkContext workContext) throws GroupInitializationException {
        Conversation conversation = workContext.peekCallFrame().getConversation();
        assert conversation != null;
        super.startContext(workContext, conversation);
    }

    public void startContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException {
        Conversation conversation = workContext.peekCallFrame().getConversation();
        assert conversation != null;
        super.startContext(workContext, conversation);
        expirationPolicies.put(conversation, policy);
    }

    public void joinContext(WorkContext workContext) throws GroupInitializationException {
        Conversation conversation = workContext.peekCallFrame().getConversation();
        assert conversation != null;
        super.joinContext(workContext, conversation);
    }

    public void joinContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException {
        Conversation conversation = workContext.peekCallFrame().getConversation();
        assert conversation != null;
        if (super.joinContext(workContext, conversation)) {
            expirationPolicies.put(conversation, policy);
        }
    }

    public void stopContext(WorkContext workContext) {
        Conversation conversation = workContext.peekCallFrame().getConversation();
        assert conversation != null;
        super.stopContext(workContext, conversation);
        expirationPolicies.remove(conversation);
        notifyExpirationCallbacks(conversation);
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, WorkContext workContext) throws InstanceLifecycleException {
        CallFrame frame = workContext.peekCallFrame();
        Conversation conversation = frame.getConversation();
        assert conversation != null;
        ExpirationPolicy policy = expirationPolicies.get(conversation);
        if (policy != null && !policy.isExpired()) {
            // renew the conversation expiration if one is associated, i.e. it is an expiring conversation
            expirationPolicies.get(conversation).renew();
        }
        ConversationContext context = frame.getConversationContext();
        // if the context is new or propagates a conversation and the target instance has not been created, create it
        boolean create = (context == ConversationContext.NEW || context == ConversationContext.PROPAGATE);
        InstanceWrapper<T> wrapper = super.getWrapper(component, workContext, conversation, create);
        if (wrapper == null) {
            // conversation has either been ended or timed out, throw an exception
            throw new ConversationEndedException("Conversation ended");
        }
        return wrapper;
    }

    public void reinject() {
    }

    public void addObjectFactory(AtomicComponent<?> component, ObjectFactory<?> factory, String referenceName, Object key) {

    }

    private void notifyExpirationCallbacks(Conversation conversation) {
        List<ConversationExpirationCallback> callbacks = expirationCallbacks.remove(conversation);
        if (callbacks != null) {
            synchronized (callbacks) {
                for (ConversationExpirationCallback callback : callbacks) {
                    callback.expire(conversation);
                }
            }
        }
    }

    /**
     * Periodically scans and removes expired conversation contexts.
     */
    private class Reaper implements Runnable {
        public void run() {
            for (Iterator<Map.Entry<Conversation, ExpirationPolicy>> iterator = expirationPolicies.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<Conversation, ExpirationPolicy> entry = iterator.next();
                if (entry.getValue().isExpired()) {
                    Conversation conversation = entry.getKey();
                    iterator.remove();
                    WorkContext workContext = new WorkContext();
                    CallFrame frame = new CallFrame(null, conversation, conversation, null);
                    workContext.addCallFrame(frame);
                    stopContext(workContext, conversation);
                    notifyExpirationCallbacks(conversation);
                }
            }
        }
    }

}
