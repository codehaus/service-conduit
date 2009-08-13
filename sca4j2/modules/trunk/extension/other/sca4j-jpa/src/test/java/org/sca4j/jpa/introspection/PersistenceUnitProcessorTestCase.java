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
package org.sca4j.jpa.introspection;

import javax.persistence.PersistenceUnit;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.introspection.impl.contract.DefaultContractProcessor;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.jpa.scdl.PersistenceUnitResource;

/**
 * @version $Rev: 3147 $ $Date: 2008-03-20 10:45:03 +0000 (Thu, 20 Mar 2008) $
 */
public class PersistenceUnitProcessorTestCase extends TestCase {

    private PersistenceUnitProcessor processor;
    private PersistenceUnit annotation;

    public void testCreateDefinition() {

        PersistenceUnitResource definition = processor.createDefinition(annotation);
        assertEquals("name", definition.getName());
        assertEquals("unitName", definition.getUnitName());
    }

    protected void setUp() throws Exception {
        super.setUp();

        annotation = EasyMock.createMock(PersistenceUnit.class);
        EasyMock.expect(annotation.name()).andReturn("name");
        EasyMock.expect(annotation.unitName()).andReturn("unitName");
        EasyMock.replay(annotation);

        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        ContractProcessor contractProcessor = new DefaultContractProcessor(helper);
        processor = new PersistenceUnitProcessor(contractProcessor);
    }
}
