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
package org.sca4j.fabric.services.componentmanager;

import java.net.URI;
import java.util.List;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.spi.component.Component;

/**
 * @version $Rev: 2950 $ $Date: 2008-02-29 14:01:59 +0000 (Fri, 29 Feb 2008) $
 */
public class ComponentManagerImplTestCase extends TestCase {
    private static final URI DOMAIN = URI.create("sca://localhost/");
    private static final URI ROOT1 = DOMAIN.resolve("root1");
    private static final URI GRANDCHILD = DOMAIN.resolve("parent/child2/grandchild");

    private ComponentManagerImpl manager;

    public void testRegister() throws Exception {
        Component root = EasyMock.createMock(Component.class);
        EasyMock.expect(root.getUri()).andReturn(ROOT1);
        EasyMock.replay(root);
        manager.register(root);
        assertEquals(root, manager.getComponent(ROOT1));
        EasyMock.verify(root);

        EasyMock.reset(root);
        EasyMock.expect(root.getUri()).andReturn(ROOT1);
        EasyMock.replay(root);
        manager.unregister(root);
        EasyMock.verify(root);
        assertEquals(null, manager.getComponent(ROOT1));
    }

    public void testRegisterGrandchild() throws Exception {
        Component root = EasyMock.createMock(Component.class);
        EasyMock.expect(root.getUri()).andReturn(GRANDCHILD);
        EasyMock.replay(root);
        manager.register(root);
        assertEquals(root, manager.getComponent(GRANDCHILD));
        EasyMock.verify(root);
    }

    public void testRegisterDuplicate() throws Exception {
        Component root = EasyMock.createMock(Component.class);
        EasyMock.expect(root.getUri()).andReturn(ROOT1);
        EasyMock.replay(root);

        Component duplicate = EasyMock.createMock(Component.class);
        EasyMock.expect(duplicate.getUri()).andReturn(ROOT1);
        EasyMock.replay(duplicate);

        manager.register(root);
        assertEquals(root, manager.getComponent(ROOT1));
        try {
            manager.register(duplicate);
            fail();
        } catch (DuplicateComponentException e) {
            // expected
        }
        assertEquals(root, manager.getComponent(ROOT1));
        EasyMock.verify(root);
        EasyMock.verify(duplicate);
    }

    public void testGetComponentsInHierarchy() throws Exception {
        Component c1 = EasyMock.createMock(Component.class);
        URI uri1 = URI.create("sca://fabric/component1");
        EasyMock.expect(c1.getUri()).andReturn(uri1).atLeastOnce();
        EasyMock.replay(c1);
        Component c2 = EasyMock.createMock(Component.class);
        URI uri2 = URI.create("sca://fabric/component2");
        EasyMock.expect(c2.getUri()).andReturn(uri2).atLeastOnce();
        EasyMock.replay(c2);
        Component c3 = EasyMock.createMock(Component.class);
        EasyMock.expect(c3.getUri()).andReturn(URI.create("sca://other/component3")).atLeastOnce();
        EasyMock.replay(c3);
        manager.register(c1);
        manager.register(c2);
        manager.register(c3);
        List<URI> uris = manager.getComponentsInHierarchy(URI.create("sca://fabric"));
        assertEquals(2, uris.size());
        assertTrue(uris.contains(uri1));
        assertTrue(uris.contains(uri2));
    }

    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComponentManagerImpl();
    }
}
