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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
