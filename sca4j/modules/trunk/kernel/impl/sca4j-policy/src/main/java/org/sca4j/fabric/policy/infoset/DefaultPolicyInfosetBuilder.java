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
