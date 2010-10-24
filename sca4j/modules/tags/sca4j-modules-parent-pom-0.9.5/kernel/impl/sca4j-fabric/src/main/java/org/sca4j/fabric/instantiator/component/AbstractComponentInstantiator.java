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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.fabric.instantiator.component;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.OperationDefinition;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.PropertyValue;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @version $Revision$ $Date$
 */
public abstract class AbstractComponentInstantiator implements ComponentInstantiator {

    private static final DocumentBuilderFactory DOCUMENT_FACTORY;
    private static final XPathFactory XPATH_FACTORY;

    static {
        DOCUMENT_FACTORY = DocumentBuilderFactory.newInstance();
        DOCUMENT_FACTORY.setNamespaceAware(true);
        XPATH_FACTORY = XPathFactory.newInstance();
    }

    private final DocumentLoader documentLoader;

    protected AbstractComponentInstantiator(DocumentLoader documentLoader) {
        this.documentLoader = documentLoader;
    }

    /**
     * Transfers intents and policy sets declared in the SCDL to the service contract.
     *
     * @param serviceDefinition Service definition from the SCDL.
     */
    protected void addOperationLevelIntentsAndPolicies(LogicalService logicalService, ServiceDefinition serviceDefinition) {
        transferIntentsAndPolicies(logicalService.getDefinition().getServiceContract(), serviceDefinition.getOperations());
    }

    /**
     * Transfers intents and policy sets declared in the SCDL to the service contract.
     *
     * @param referenceDefinition Reference definition from the SCDL.
     */
    protected void addOperationLevelIntentsAndPolicies(LogicalReference logicalReference, ReferenceDefinition referenceDefinition) {
        transferIntentsAndPolicies(logicalReference.getDefinition().getServiceContract(), referenceDefinition.getOperations());
    }

    /**
     * Set the initial actual property values of a component.
     *
     * @param component  the component to initialize
     * @param definition the definition of the component
     * @param change     the logical change
     */
    protected <I extends Implementation<?>> void initializeProperties(LogicalComponent<I> component,
                                                                      ComponentDefinition<I> definition,
                                                                      LogicalChange change) {

        Map<String, PropertyValue> propertyValues = definition.getPropertyValues();
        AbstractComponentType<?, ?, ?, ?> componentType = definition.getComponentType();

        for (Property property : componentType.getProperties().values()) {

            String name = property.getName();
            PropertyValue propertyValue = propertyValues.get(name);
            Document value;

            if (propertyValue == null) {
                // use default value from component type
                value = property.getDefaultValue();
            } else {
                // the spec defines the following sequence
                if (propertyValue.getFile() != null) {
                    // load the value from an external resource
                    value = loadValueFromFile(component, property.getName(), propertyValue.getFile(), change);
                } else if (propertyValue.getSource() != null) {
                    // get the value by evaluating an XPath against the composite properties
                    try {
                        value = deriveValueFromXPath(propertyValue.getSource(), component.getParent());
                    } catch (XPathExpressionException e) {
                        InvalidProperty error = new InvalidProperty(component.getUri(), name, e);
                        change.addError(error);
                        return;
                    }
                } else {
                    // use inline XML file
                    value = propertyValue.getValue();
                }

            }
            if (property.isRequired() && value == null) {
                // The XPath expression returned an empty value. Since the property is required, throw an exception
                PropertySourceNotFound error = new PropertySourceNotFound(component.getUri(), name);
                change.addError(error);
            } else if (!property.isRequired() && value == null) {
                // The XPath expression returned an empty value. Since the property is optional, ignore it
                continue;
            } else {
                // set the property value
                component.setPropertyValue(name, value);
            }

        }

    }

    Document deriveValueFromXPath(String source, final LogicalComponent<?> parent) throws XPathExpressionException {

        XPathVariableResolver variableResolver = new XPathVariableResolver() {
            public Object resolveVariable(QName qName) {
                String name = qName.getLocalPart();
                Document value = parent.getPropertyValue(name);
                if (value == null) {
                    return null;
                }
                return value.getDocumentElement();
            }
        };

        XPath xpath = XPATH_FACTORY.newXPath();
        xpath.setXPathVariableResolver(variableResolver);

        DocumentBuilder builder;
        try {
            builder = DOCUMENT_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }

        Document value = builder.newDocument();
        Element root = value.createElement("value");
        // TODO do we need to copy namespace declarations to this root
        value.appendChild(root);
        try {
            NodeList result = (NodeList) xpath.evaluate(source, root, XPathConstants.NODESET);
            if (result.getLength() == 0) {
                return null;
            }
            for (int i = 0; i < result.getLength(); i++) {
                Node node = result.item(i);
                // clone the node as the original may be accessed multiple times
                Node cloned = node.cloneNode(true);
                value.adoptNode(cloned);
                short type = cloned.getNodeType();
                if (Node.ELEMENT_NODE == type || Node.TEXT_NODE == type) {
                    root.appendChild(cloned);
                } else if (Node.ATTRIBUTE_NODE == type) {
                    // convert the attribute to an element in the property DOM
                    Element element = value.createElement(cloned.getNodeName());
                    element.setTextContent(cloned.getNodeValue());
                    root.appendChild(element);
                } else {
                    throw new XPathExpressionException("Unsupported node type: " + type);
                }
            }
        } catch (XPathExpressionException e) {
            // FIXME rethrow this for now, fix if people find it confusing
            // the Apache and Sun implementations of XPath throw a nested NullPointerException
            // if the xpath contains an unresolvable variable. It might be better to throw
            // a more descriptive cause, but that also might be confusing for people who
            // are used to this behaviour
            throw e;
        }
        return value;

    }

    private Document loadValueFromFile(LogicalComponent<?> parent, String name, URI file, LogicalChange change) {
        try {
            return documentLoader.load(file);
        } catch (IOException e) {
            InvalidPropertyFile error = new InvalidPropertyFile(parent.getUri(), name, e, file);
            change.addError(error);
            return null;
        } catch (SAXException e) {
            InvalidPropertyFile error = new InvalidPropertyFile(parent.getUri(), name, e, file);
            change.addError(error);
            return null;
        }
    }

    private void transferIntentsAndPolicies(ServiceContract serviceContract, List<OperationDefinition> operationDefinitions) {
        for (OperationDefinition operationDefinition : operationDefinitions) {
            for (Operation operation : serviceContract.getOperations()) {
                if (operationDefinition.getName().equals(operation.getName())) {
                    for (QName intent : operationDefinition.getIntents()) {
                        operation.addIntent(intent);
                    }
                    for (QName policySet : operationDefinition.getPolicySets()) {
                        operation.addPolicySet(policySet);
                    }
                }
            }
        }
    }

}
