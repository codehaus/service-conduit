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
package org.sca4j.tests.mock;

import junit.framework.TestCase;

import org.easymock.IMocksControl;
import org.oasisopen.sca.annotation.Reference;

/**
 * @version $Revision$ $Date$
 */
public class MockTest extends TestCase {
    
    private static final String ARG1 = "VALUE1";
    private static final String ARG2 = "VALUE2";
    private static final Long ARG3 = 1L;
    
    private OverloadedService mockedOverloadedService;
    
    private MockService1 mockService1;
    private MockService2 mockService2;
    private UserComponent userComponent;
    private IMocksControl control;
    
    @Reference
    public void setControl(IMocksControl control) {
        this.control = control;
    }
    
    @Reference
    public void setMockService1(MockService1 mockService1) {
        this.mockService1 = mockService1;
    }
    
    @Reference
    public void setMockService2(MockService2 mockService2) {
        this.mockService2 = mockService2;
    }
    
    @Reference
    public void setUserComponent(UserComponent userComponent) {
        this.userComponent = userComponent;
    }
    
    @Reference
    public void setOverloadedService(OverloadedService mockedOverloadedService) {
        this.mockedOverloadedService = mockedOverloadedService;
    }    
    
    public void testMock() {
        
        control.reset();
        
        mockService1.doMock1("test");
        mockService2.doMock2(1);
        mockService2.doMock0(1);
        
        control.replay();
        
        userComponent.userMethod();
        
        control.verify();
        
    }
    
    public void testNoMock() {
        
        control.reset();
        
        mockService1.doMock1("test");
        mockService2.doMock2(1);
        
        control.replay();

        // fail after the try block as we don't want to catch the AssertionError it would throw
        boolean fail = true;
        try {
            control.verify();
        } catch (AssertionError e) {
            fail = false;
        }
        if (fail) {
            fail("Expected an error");
        }
    }

    public void testUsingControlToCreateMock() {
        control.reset();
        MockService0 mock = control.createMock(MockService0.class);
        mock.doMock0(1);
        control.replay();
        mock.doMock0(1);
        control.verify();
    }
    
    public void testMockingOverloadedInvocation() throws Exception {
        
        control.reset();
        
        mockedOverloadedService.doWork(ARG1, ARG2);
        mockedOverloadedService.doWork(ARG1, ARG2, ARG3);        
        control.replay();
        
        mockedOverloadedService.doWork(ARG1, ARG2);
        mockedOverloadedService.doWork(ARG1, ARG2, ARG3);        
        control.verify();        
    }   

}
