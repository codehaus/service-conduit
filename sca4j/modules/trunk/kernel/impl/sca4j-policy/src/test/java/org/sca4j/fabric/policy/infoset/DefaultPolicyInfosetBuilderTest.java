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
package org.sca4j.fabric.policy.infoset;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class DefaultPolicyInfosetBuilderTest extends TestCase {

    public void testBuildInfoSetLogicalBinding() {
        
        Element element = new DefaultPolicyInfosetBuilder().buildInfoSet(getTestBinding());
        
        assertEquals("service", element.getNodeName());
        assertEquals("testService", element.getAttribute("name"));
        assertEquals("binding.test", element.getFirstChild().getNodeName());
        assertEquals("component", element.getParentNode().getNodeName());
        assertEquals("testComponent", element.getParentNode().getAttributes().getNamedItem("name").getNodeValue());
        
    }

    public void testBuildInfoSetLogicalComponent() {
        
        Element element = new DefaultPolicyInfosetBuilder().buildInfoSet(getTestComponent());
        
        assertEquals("component", element.getNodeName());
        assertEquals("testComponent", element.getAttribute("name"));
        assertEquals("implementation.test", element.getFirstChild().getNodeName());
        
    }
    
    static LogicalComponent<?> getTestComponent() {
        
        TestComponentType componentType = new TestComponentType();
        TestImplementation implementation = new TestImplementation(componentType);
        ComponentDefinition<TestImplementation> componentDefinition = new ComponentDefinition<TestImplementation>("testComponent", implementation);

        return new LogicalComponent<TestImplementation>(null, null, componentDefinition, null);
    }
    
    static LogicalBinding<?> getTestBinding() {
        
        TestComponentType componentType = new TestComponentType();
        TestImplementation implementation = new TestImplementation(componentType);
        ComponentDefinition<TestImplementation> componentDefinition = new ComponentDefinition<TestImplementation>("testComponent", implementation);
        LogicalComponent<TestImplementation> logicalComponent = new LogicalComponent<TestImplementation>(null, null, componentDefinition, null);
        
        ReferenceDefinition referenceDefinition = new ReferenceDefinition("testService", null);
        LogicalReference logicalReference = new LogicalReference(null, referenceDefinition, logicalComponent);
        
        TestBinidingDefinition testBinidingDefinition = new TestBinidingDefinition();
        LogicalBinding<TestBinidingDefinition> logicalBinding = new LogicalBinding<TestBinidingDefinition>(testBinidingDefinition, logicalReference);
        logicalReference.addBinding(logicalBinding);
        
        return logicalBinding;
        
    }
    
    @SuppressWarnings({"serial"})
    private static class TestBinidingDefinition extends BindingDefinition {
        private TestBinidingDefinition() {
            super(null, new QName("binding.test"), null);
        }
    }
    
    @SuppressWarnings({"serial"})
    private static class TestComponentType extends AbstractComponentType<ServiceDefinition, ReferenceDefinition, Property, ResourceDefinition> {
        
    }
    
    @SuppressWarnings({"serial"})
    private static class TestImplementation extends Implementation<TestComponentType> {
        
        private TestImplementation(TestComponentType componentType) {
            super(componentType);
        }
        
        @Override
        public QName getType() {
            return new QName("implementation.test");
        }
    }

}
