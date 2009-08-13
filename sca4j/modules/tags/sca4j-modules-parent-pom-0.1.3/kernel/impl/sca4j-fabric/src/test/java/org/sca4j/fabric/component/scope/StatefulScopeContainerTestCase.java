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

import junit.framework.TestCase;
import org.easymock.IMocksControl;
import org.easymock.classextension.EasyMock;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.ExpirationPolicy;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.InstanceWrapperStore;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 4786 $ $Date: 2008-06-08 14:37:24 +0100 (Sun, 08 Jun 2008) $
 */
public class StatefulScopeContainerTestCase extends TestCase {
    private StatefulScopeContainer<MockId> container;
    private IMocksControl control;
    private InstanceWrapperStore<MockId> store;
    private Scope<MockId> scope;
    private MockId conversation;
    private WorkContext workContext;
    private AtomicComponent<Object> component;
    private InstanceWrapper<Object> wrapper;

    public void testCorrectScope() {
        assertSame(scope, container.getScope());
    }

    public void testStoreIsNotifiedOfContextStartStop() throws GroupInitializationException {
        store.startContext(conversation);
        store.stopContext(conversation);
        control.replay();
        container.startContext(workContext);
        container.stopContext(workContext);
        control.verify();
    }

    public void testWrapperCreatedIfNotFound() throws Exception {
        EasyMock.expect(store.getWrapper(component, conversation)).andReturn(null);
        EasyMock.expect(component.createInstanceWrapper(workContext)).andReturn(wrapper);
        wrapper.start(workContext);
        store.putWrapper(component, conversation, wrapper);
        store.startContext(EasyMock.eq(conversation));
        control.replay();
        container.startContext(workContext);
        assertSame(wrapper, container.getWrapper(component, workContext));
        control.verify();
    }

    public void testWrapperReturnedIfFound() throws InstanceLifecycleException {
        EasyMock.expect(store.getWrapper(component, conversation)).andReturn(wrapper);
        control.replay();
        assertSame(wrapper, container.getWrapper(component, workContext));
        control.verify();
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        conversation = new MockId("contextId");
        scope = new Scope<MockId>("TESTING", MockId.class);
        control = EasyMock.createControl();
        store = control.createMock(InstanceWrapperStore.class);
        workContext = new WorkContext();
        workContext.addCallFrame(new CallFrame(conversation));
        component = control.createMock(AtomicComponent.class);
        wrapper = control.createMock(InstanceWrapper.class);
        container = new StatefulScopeContainer<MockId>(scope, null, store) {

            public void startContext(WorkContext workContext) throws GroupInitializationException {
                super.startContext(workContext, conversation);
            }

            public void startContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException {

            }

            public void joinContext(WorkContext workContext) throws GroupInitializationException {

            }

            public void joinContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException {

            }

            public void stopContext(WorkContext workContext) {
                super.stopContext(workContext, conversation);
            }

            public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, WorkContext workContext) throws InstanceLifecycleException {
                return super.getWrapper(component, workContext, conversation, true);
            }

            public void reinject() {
            }

            public void addObjectFactory(AtomicComponent<?> component, ObjectFactory<?> factory, String referenceName, Object key) {
            }

        };
    }

    private class MockId {
        private String id;

        public MockId(String id) {
            this.id = id;
        }

        public Object getId() {
            return id;
        }

    }
}
