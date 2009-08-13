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
package org.sca4j.system.introspection;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionException;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * @version $Rev: 3105 $ $Date: 2008-03-15 16:47:31 +0000 (Sat, 15 Mar 2008) $
 */
public class SystemImplementationProcessorImplTestCase extends TestCase {
    private SystemImplementationProcessorImpl loader;
    private ClassWalker<SystemImplementation> classWalker;
    private IntrospectionContext context;
    private SystemImplementation impl;
    private HeuristicProcessor<SystemImplementation> heuristic;
    private IMocksControl control;

    public void testSimple() throws IntrospectionException {
        impl.setImplementationClass(Simple.class.getName());

        classWalker.walk(EasyMock.same(impl), EasyMock.eq(Simple.class), EasyMock.isA(IntrospectionContext.class));
        heuristic.applyHeuristics(EasyMock.same(impl), EasyMock.eq(Simple.class), EasyMock.isA(IntrospectionContext.class));
        control.replay();
        loader.introspect(impl, context);

        PojoComponentType componentType = impl.getComponentType();
        assertNotNull(componentType);
        assertEquals(Simple.class.getName(), componentType.getImplClass());
        control.verify();
    }

    private static class Simple {
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        impl = new SystemImplementation();
        IntrospectionHelper helper = new DefaultIntrospectionHelper();


        context = EasyMock.createNiceMock(IntrospectionContext.class);
        EasyMock.expect(context.getTargetClassLoader()).andStubReturn(getClass().getClassLoader());
        EasyMock.replay(context);

        control = EasyMock.createControl();
        classWalker = control.createMock(ClassWalker.class);
        heuristic = control.createMock(HeuristicProcessor.class);

        this.loader = new SystemImplementationProcessorImpl(classWalker, heuristic, helper);
    }
}
