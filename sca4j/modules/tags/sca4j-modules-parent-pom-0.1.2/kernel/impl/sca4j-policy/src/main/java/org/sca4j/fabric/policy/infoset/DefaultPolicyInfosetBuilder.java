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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sca4j.scdl.Implementation;
import org.sca4j.spi.model.instance.Bindable;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class DefaultPolicyInfosetBuilder implements PolicyInfosetBuilder {
    
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    /**
     * The infoset for the binding includes, the binding, service or reference against which
     * the binding is specified, the component on which the service or reference is declared.
     * 
     * <component name="">
     *   <service|reference name="">
     *     <binding.xxx/>
     *   </service|reference>
     * </component>
     * 
     * The returned element is the element that represents the service or reference.
     */
    public Element buildInfoSet(LogicalBinding<?> logicalBinding) {
        
        try {

            DocumentBuilder builder = FACTORY.newDocumentBuilder();
            Document document = builder.newDocument();
            
            Element componentElement = document.createElement("component");
            
            Element bindableElement = null;
            Bindable bindable = logicalBinding.getParent();
            if (bindable instanceof LogicalService) {
                LogicalService logicalService = (LogicalService) bindable;
                bindableElement = document.createElement("service");
                bindableElement.setAttribute("name", logicalService.getDefinition().getName());
            } else {
                LogicalReference logicalReference = (LogicalReference) bindable;
                bindableElement = document.createElement("reference");
                bindableElement.setAttribute("name", logicalReference.getDefinition().getName());
            }
            componentElement.appendChild(bindableElement);
            
            LogicalComponent<?> logicalComponent = bindable.getParent();
            componentElement.setAttribute("name", logicalComponent.getDefinition().getName());
            
            Element bindingElement = document.createElement(logicalBinding.getBinding().getType().getLocalPart());
            bindableElement.appendChild(bindingElement);

            return bindableElement;
            
        } catch (ParserConfigurationException ex) {
            throw new AssertionError(ex);
        }
        
    }

    /**
     * The infoset for the implementation includes the component and implementation type..
     * 
     * <component name="">
     *   <impelemnation.xxx/>
     * </component>
     * 
     * The returned element is the element that represents the component.
     */
    public Element buildInfoSet(LogicalComponent<?> logicalComponent) {
        
        try {

            DocumentBuilder builder = FACTORY.newDocumentBuilder();
            Document document = builder.newDocument();
            
            Element componentElement = document.createElement("component");
            componentElement.setAttribute("name", logicalComponent.getDefinition().getName());
            
            Implementation<?> implementation = logicalComponent.getDefinition().getImplementation();
            Element implementationElement = document.createElement(implementation.getType().getLocalPart());
            
            componentElement.appendChild(implementationElement);

            return componentElement;
            
        } catch (ParserConfigurationException ex) {
            throw new AssertionError(ex);
        }
        
    }

}
