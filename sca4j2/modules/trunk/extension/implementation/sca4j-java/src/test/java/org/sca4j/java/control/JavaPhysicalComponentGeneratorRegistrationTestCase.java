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
package org.sca4j.java.control;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.spi.generator.GeneratorRegistry;

/**
 * @version $Rev: 4888 $ $Date: 2008-06-23 16:26:36 +0100 (Mon, 23 Jun 2008) $
 */
public class JavaPhysicalComponentGeneratorRegistrationTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testRegistration() throws Exception {
        GeneratorRegistry registry = EasyMock.createMock(GeneratorRegistry.class);
        registry.register(EasyMock.isA(Class.class),
                          EasyMock.isA(JavaComponentGenerator.class));
        EasyMock.replay(registry);
        JavaComponentGenerator generator = new JavaComponentGenerator(registry, null, null);
        generator.init();
        EasyMock.verify(registry);
    }

}
