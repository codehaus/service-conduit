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
package org.sca4j.timer.component.runtime;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.InvocationRuntimeException;

/**
 * Implementation registered with the runtime TimerService to receive notifications and invoke a component instance when a trigger has fired.
 *
 * @version $Revision$ $Date$
 */
public class TimerComponentInvoker<T> implements Runnable {
    private TimerComponent<T> component;
    private ScopeContainer<?> scopeContainer;

    public TimerComponentInvoker(TimerComponent<T> component) {
        this.component = component;
        this.scopeContainer = component.getScopeContainer();
    }

    public void run() {
        // create a new work context
        WorkContext workContext = new WorkContext();
        CallFrame frame = new CallFrame();
        workContext.addCallFrame(frame);
        InstanceWrapper<T> wrapper;
        try {
            // TODO handle conversations
            //startOrJoinContext(workContext);
            wrapper = scopeContainer.getWrapper(component, workContext);
        } catch (InstanceLifecycleException e) {
            throw new InvocationRuntimeException(e);
        }

        try {
            Object instance = wrapper.getInstance();
            assert instance instanceof Runnable;  // all timer components must implement java.lang.Runnable
            WorkContext oldWorkContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
            try {
                ((Runnable) instance).run();
            } finally {
                PojoWorkContextTunnel.setThreadWorkContext(oldWorkContext);
            }
        } finally {
            try {
                scopeContainer.returnWrapper(component, workContext, wrapper);
                // TODO handle conversations
            } catch (InstanceDestructionException e) {
                //noinspection ThrowFromFinallyBlock
                throw new InvocationRuntimeException(e);
            }
        }

    }
}
