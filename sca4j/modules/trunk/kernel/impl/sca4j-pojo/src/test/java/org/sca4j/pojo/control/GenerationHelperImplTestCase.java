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
package org.sca4j.pojo.control;

import java.util.Map;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.Signature;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * @version $Rev: 5246 $ $Date: 2008-08-20 22:30:18 +0100 (Wed, 20 Aug 2008) $
 */
public class GenerationHelperImplTestCase extends TestCase {

    private InstanceFactoryGenerationHelper helper;
    private InstanceFactoryDefinition providerDefinition;
    private LogicalComponent<MockImplementation> logicalComponent;
    private ComponentDefinition<MockImplementation> componentDefinition;
    private MockImplementation implementation;
    private PojoComponentType componentType;
    private InjectableAttribute intProp;
    private InjectableAttribute stringProp;

    public void testSimpleConstructor() {
        Signature constructor = new Signature("Test", "int", "String");
        ConstructorInjectionSite intSite = new ConstructorInjectionSite(constructor, 0);
        ConstructorInjectionSite stringSite = new ConstructorInjectionSite(constructor, 1);
        componentType.setConstructor(constructor);
        componentType.addInjectionSite(intProp, intSite);
        componentType.addInjectionSite(stringProp, stringSite);
        helper.processInjectionSites(logicalComponent, providerDefinition);
        Map<InjectionSite, InjectableAttribute> mapping = providerDefinition.getConstruction();
        assertEquals(intProp, mapping.get(intSite));
        assertEquals(stringProp, mapping.get(stringSite));
        assertTrue(providerDefinition.getPostConstruction().isEmpty());
        assertTrue(providerDefinition.getReinjection().isEmpty());
    }

    protected void setUp() throws Exception {
        super.setUp();

        helper = new GenerationHelperImpl();
        componentType = new PojoComponentType(null);
        implementation = new MockImplementation(componentType);
        componentDefinition = new ComponentDefinition<MockImplementation>("mock", implementation);
        logicalComponent = new LogicalComponent<MockImplementation>(null, null, componentDefinition, null);
        providerDefinition = new InstanceFactoryDefinition();

        intProp = new InjectableAttribute(InjectableAttributeType.PROPERTY, "intProp");
        stringProp = new InjectableAttribute(InjectableAttributeType.PROPERTY, "stringProp");
    }

    private static class MockImplementation extends Implementation<PojoComponentType> {
        private MockImplementation(PojoComponentType componentType) {
            super(componentType);
        }

        public QName getType() {
            return null;
        }
    }
}
