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
package org.sca4j.java.introspection;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionException;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.java.scdl.JavaImplementation;
import org.sca4j.pojo.scdl.PojoComponentType;

/**
 * @version $Rev: 3105 $ $Date: 2008-03-15 16:47:31 +0000 (Sat, 15 Mar 2008) $
 */
public class JavaComponentTypeLoaderTestCase extends TestCase {

    private JavaImplementationProcessorImpl loader;
    private ClassWalker<JavaImplementation> classWalker;
    private IntrospectionContext context;
    private JavaImplementation impl;
    private HeuristicProcessor<JavaImplementation> heuristic;
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
        ClassLoader cl = getClass().getClassLoader();
        impl = new JavaImplementation();
        IntrospectionHelper helper = EasyMock.createNiceMock(IntrospectionHelper.class);
        EasyMock.expect(helper.loadClass(Simple.class.getName(), cl)).andStubReturn(Simple.class);
        EasyMock.expect(helper.mapTypeParameters(Simple.class)).andStubReturn(null);
        EasyMock.replay(helper);


        context = EasyMock.createNiceMock(IntrospectionContext.class);
        EasyMock.expect(context.getTargetClassLoader()).andStubReturn(cl);
        EasyMock.replay(context);

        control = EasyMock.createControl();
        classWalker = control.createMock(ClassWalker.class);
        heuristic = control.createMock(HeuristicProcessor.class);

        this.loader = new JavaImplementationProcessorImpl(classWalker, heuristic, helper);
    }
}
