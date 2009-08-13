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
package org.sca4j.transform;

import junit.framework.TestCase;
import org.w3c.dom.Node;

import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.spi.model.type.XSDSimpleType;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;
import org.sca4j.transform.dom2java.String2Integer;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
public class DefaultTransformerRegistryTestCase extends TestCase {
    private TransformerRegistry<PullTransformer<?,?>> registry;

    public void testRegistration() {
        PullTransformer<?,?> transformer = new String2Integer();
        registry.register(transformer);
        XSDSimpleType source = new XSDSimpleType(Node.class, XSDSimpleType.STRING);
        JavaClass<Integer> target = new JavaClass<Integer>(Integer.class);
        assertSame(transformer, registry.getTransformer(source, target));
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new DefaultTransformerRegistry<PullTransformer<?,?>>();
    }
}
