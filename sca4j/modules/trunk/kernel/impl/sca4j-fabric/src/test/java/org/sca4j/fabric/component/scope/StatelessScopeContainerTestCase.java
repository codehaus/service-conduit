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

import junit.framework.TestCase;
import org.easymock.IMocksControl;
import org.easymock.classextension.EasyMock;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Unit tests for the composite scope container
 *
 * @version $Rev: 3020 $ $Date: 2008-03-04 03:16:33 +0000 (Tue, 04 Mar 2008) $
 */
public class StatelessScopeContainerTestCase<T> extends TestCase {
    private StatelessScopeContainer scopeContainer;
    private IMocksControl control;
    private AtomicComponent<T> component;
    private InstanceWrapper<T> wrapper;
    private WorkContext workContext;

    public void testCorrectScope() {
        assertEquals(Scope.STATELESS, scopeContainer.getScope());
    }

    public void testInstanceCreation() throws Exception {
        @SuppressWarnings("unchecked")
        InstanceWrapper<T> wrapper2 = control.createMock(InstanceWrapper.class);

        EasyMock.expect(component.createInstanceWrapper(workContext)).andReturn(wrapper);
        wrapper.start(workContext);
        EasyMock.expect(component.createInstanceWrapper(workContext)).andReturn(wrapper2);
        wrapper2.start(workContext);
        control.replay();

        assertSame(wrapper, scopeContainer.getWrapper(component, workContext));
        assertSame(wrapper2, scopeContainer.getWrapper(component, workContext));
        control.verify();
    }

    public void testReturnWrapper() throws Exception {
        wrapper.stop(workContext);
        control.replay();
        scopeContainer.returnWrapper(component, workContext, wrapper);
        control.verify();
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        scopeContainer = new StatelessScopeContainer(EasyMock.createNiceMock(ScopeContainerMonitor.class));

        control = EasyMock.createStrictControl();
        workContext = control.createMock(WorkContext.class);
        component = control.createMock(AtomicComponent.class);
        wrapper = control.createMock(InstanceWrapper.class);
    }
}
