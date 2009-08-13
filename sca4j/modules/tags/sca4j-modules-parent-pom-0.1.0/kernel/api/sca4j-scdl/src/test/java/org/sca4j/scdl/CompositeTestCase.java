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
