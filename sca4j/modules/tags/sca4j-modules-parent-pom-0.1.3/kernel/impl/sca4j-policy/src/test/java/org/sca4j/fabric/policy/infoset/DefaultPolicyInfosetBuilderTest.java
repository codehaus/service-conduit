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
