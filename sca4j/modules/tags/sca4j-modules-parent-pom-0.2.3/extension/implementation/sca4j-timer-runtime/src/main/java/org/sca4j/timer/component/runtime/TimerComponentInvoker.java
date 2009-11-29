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
