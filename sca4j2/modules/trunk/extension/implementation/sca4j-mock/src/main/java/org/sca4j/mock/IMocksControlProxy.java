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
package org.sca4j.mock;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IExpectationSetters;
import org.easymock.IMocksControl;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;

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
