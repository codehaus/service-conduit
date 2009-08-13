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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.fabric.component.scope;

import java.net.URI;

import junit.framework.TestCase;
import org.easymock.IMocksControl;
import org.easymock.classextension.EasyMock;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.invocation.CallFrame;

/**
 * @version $$Rev: 3566 $$ $$Date: 2008-04-05 02:26:37 +0100 (Sat, 05 Apr 2008) $$
 */
public class CompositeScopeContainerTestCase<T> extends TestCase {
    protected IMocksControl control;
    protected ScopeContainer<URI> scopeContainer;
    protected URI groupId;
    protected AtomicComponent<T> component;
    protected InstanceWrapper<T> wrapper;
    private WorkContext workContext;

    public void testCorrectScope() {
        assertEquals(Scope.COMPOSITE, scopeContainer.getScope());
    }

    public void testWrapperCreation() throws Exception {

        EasyMock.expect(component.isEagerInit()).andStubReturn(false);
        EasyMock.expect(component.createInstanceWrapper(workContext)).andReturn(wrapper);
        EasyMock.expect(wrapper.isStarted()).andReturn(false);
        wrapper.start(EasyMock.isA(WorkContext.class));
        EasyMock.expect(component.getGroupId()).andStubReturn(groupId);
        control.replay();
        scopeContainer.register(component);
        scopeContainer.startContext(workContext);
        assertSame(wrapper, scopeContainer.getWrapper(component, workContext));
        assertSame(wrapper, scopeContainer.getWrapper(component, workContext));
        control.verify();
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        groupId = URI.create("groupId");
        control = EasyMock.createStrictControl();
        workContext = new WorkContext();
        workContext.addCallFrame(new CallFrame(groupId));
        component = control.createMock(AtomicComponent.class);
        wrapper = control.createMock(InstanceWrapper.class);
        scopeContainer = new CompositeScopeContainer(EasyMock.createNiceMock(ScopeContainerMonitor.class));
        scopeContainer.start();
    }
}
