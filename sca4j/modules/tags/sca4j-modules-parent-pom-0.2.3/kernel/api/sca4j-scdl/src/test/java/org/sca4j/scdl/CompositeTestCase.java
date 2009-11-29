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
package org.sca4j.scdl;

import java.util.Collection;
import java.net.URI;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

/**
 * @version $Rev: 3640 $ $Date: 2008-04-15 00:23:34 +0100 (Tue, 15 Apr 2008) $
 */
public class CompositeTestCase extends TestCase {
    private QName name;
    private TestServiceContract autowireContract;

    public void testAutowireTargets() {
        InjectingComponentType ct1 = new InjectingComponentType();
        ct1.add(new ServiceDefinition("service1", autowireContract));
        TestImplementation impl1 = new TestImplementation();
        impl1.setComponentType(ct1);
        ComponentDefinition<TestImplementation> component1 = new ComponentDefinition<TestImplementation>("component1", impl1);

        Composite composite = new Composite(name);
        composite.add(component1);

        Collection<URI> targets = composite.getTargets(autowireContract);
        assertEquals(1, targets.size());
        assertTrue(targets.contains(URI.create("component1#service1")));
    }

    protected void setUp() throws Exception {
        super.setUp();
        name = new QName("name");
        autowireContract = new TestServiceContract(AutowireContract.class);
    }

    private static interface AutowireContract {
    }
}
