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
package org.sca4j.tests.mock;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.osoa.sca.annotations.Reference;

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
