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
package org.sca4j.pojo.reflection;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;

import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceInitializationException;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 5318 $ $Date: 2008-09-01 22:48:11 +0100 (Mon, 01 Sep 2008) $
 */
public class ReflectiveInstanceWrapperTestCase extends TestCase {
    private ReflectiveInstanceWrapper<Object> wrapper;
    private Object instance;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private ClassLoader cl;

    public void testWithNoCallbacks() {
        wrapper = new ReflectiveInstanceWrapper<Object>(instance, false, cl, null, null, null, null);
        WorkContext context = new WorkContext();
        try {
            wrapper.start(context);
        } catch (InstanceInitializationException e) {
            fail();
        }
        try {
            wrapper.stop(context);
        } catch (InstanceDestructionException e) {
            fail();
        }
    }

    public void testWithStartCallback() throws ObjectCallbackException {
        initInvoker.invokeEvent(instance);
        EasyMock.replay(initInvoker);
        wrapper = new ReflectiveInstanceWrapper<Object>(instance,false, cl, initInvoker, null, null, null);
        try {
            wrapper.start(new WorkContext());
        } catch (InstanceInitializationException e) {
            fail();
        }
        EasyMock.verify(initInvoker);
    }

    public void testWithStopCallback() throws ObjectCallbackException {
        destroyInvoker.invokeEvent(instance);
        EasyMock.replay(destroyInvoker);
        wrapper = new ReflectiveInstanceWrapper<Object>(instance, false, cl, null, destroyInvoker, null, null);
        try {
        	WorkContext workContext = new WorkContext();
            wrapper.start(workContext);
            wrapper.stop(workContext);
        } catch (InstanceDestructionException e) {
            fail();
        } catch (InstanceInitializationException e) {
            fail();
        }
        EasyMock.verify(destroyInvoker);
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        cl = getClass().getClassLoader();
        instance = new Object();
        initInvoker = createMock(EventInvoker.class);
        destroyInvoker = createMock(EventInvoker.class);
    }
}
