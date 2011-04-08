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
package org.sca4j.mock;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IExpectationSetters;
import org.easymock.IMocksControl;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class IMocksControlProxy implements IMocksControl {
    
    private IMocksControl delegate;
    private Map<Class<?>, Object> mocks = new HashMap<Class<?>, Object>();
    
    @Init
    public void init() {
        delegate = EasyMock.createControl();
    }

    public void checkOrder(boolean state) {
        delegate.checkOrder(state);
    }

    public <T> T createMock(Class<T> toMock) {
        
        Object mock = mocks.get(toMock);
        if (mock == null) {
            mock = delegate.createMock(toMock);
            mocks.put(toMock, mock);
        }
        return (T) mock;
        
    }

    public void replay() {
        delegate.replay();
    }

    public void reset() {
        delegate.reset();
        mocks.clear();
    }

    public void verify() {
        delegate.verify();
    }

    public IExpectationSetters andAnswer(IAnswer answer) {
        return delegate.andAnswer(answer);
    }

    public IExpectationSetters andReturn(Object value) {
        return delegate.andReturn(value);
    }

    public void andStubAnswer(IAnswer answer) {
        delegate.andStubAnswer(answer);
    }

    public void andStubReturn(Object value) {
        delegate.andStubReturn(value);
    }

    public void andStubThrow(Throwable throwable) {
        delegate.andStubThrow(throwable);
    }

    public IExpectationSetters andThrow(Throwable throwable) {
        return delegate.andThrow(throwable);
    }

    public IExpectationSetters anyTimes() {
        return delegate.anyTimes();
    }

    public void asStub() {
        delegate.asStub();
    }

    public IExpectationSetters atLeastOnce() {
        return delegate.atLeastOnce();
    }

    public IExpectationSetters once() {
        return delegate.once();
    }

    public IExpectationSetters times(int count) {
        return delegate.times(count);
    }

    public IExpectationSetters times(int min, int max) {
        return delegate.times(min, max);
    }

}
