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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.loader.composite;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.sca4j.host.Namespaces;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.introspection.xml.UnrecognizedElement;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.ComponentService;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.PropertyValue;

/**
 * Loads a component definition from an XML-based assembly file
 *
 * @version $Rev: 5135 $ $Date: 2008-08-02 07:47:16 +0100 (Sat, 02 Aug 2008) $
 */
public class ComponentLoader implements TypeLoader<ComponentDefinition<?>> {

    private static final QName COMPONENT = new QName(SCA_NS, "component");
    private static final QName PROPERTY = new QName(SCA_NS, "property");
    private static final QName SERVICE = new QName(SCA_NS, "service");
    private static final QName REFERENCE = new QName(SCA_NS, "reference");
    private static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();

    static {
        ATTRIBUTES.put("name", "name");
        ATTRIBUTES.put("autowire", "autowire");
        ATTRIBUTES.put("requires", "requires");
        ATTRIBUTES.put("policySets", "policySets");
        ATTRIBUTES.put("key", "key");
        ATTRIBUTES.put("initLevel", "initLevel");
        ATTRIBUTES.put("runtimeId", "runtimeId");
        ATTRIBUTES.put("promoted", "promoted");
    }

    private final Loader loader;
    private final TypeLoader<PropertyValue> propertyValueLoader;
    private final TypeLoader<ComponentReference> referenceLoader;
    private final TypeLoader<ComponentService> serviceLoader;
    private final LoaderHelper loaderHelper;

    public ComponentLoader(@Reference Loader loader,
                           @Reference(name = "propertyValue")TypeLoader<PropertyValue> propertyValueLoader,
                           @Reference(name = "reference")TypeLoader<ComponentReference> referenceLoader,
                           @Reference(name = "service")TypeLoader<ComponentService> serviceLoader,
                           @Reference(name = "loaderHelper")LoaderHelper loaderHelper) {
        this.loader = loader;
        this.propertyValueLoader = propertyValueLoader;
        this.referenceLoader = referenceLoader;
        this.serviceLoader = serviceLoader;
        this.loaderHelper = loaderHelper;
    }

    public ComponentDefinition<?> load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);
        String name = reader.getAttributeValue(null, "name");
        if (name == null) {
            MissingAttribute failure = new MissingAttribute("Component name not specified", "name", reader);
            context.addError(failure);
            return null;
        }

        ComponentDefinition<Implementation<?>> componentDefinition = new ComponentDefinition<Implementation<?>>(name);
        Autowire autowire = Autowire.fromString(reader.getAttributeValue(null, "autowire"));
        componentDefinition.setAutowire(autowire);
        loadRuntimeId(componentDefinition, reader, context);
        loadInitLevel(componentDefinition, reader, context);
        loadKey(componentDefinition, reader);
        loadPromoted(componentDefinition, reader);

        loaderHelper.loadPolicySetsAndIntents(componentDefinition, reader, context);

        Implementation<?> impl;
        try {
            reader.nextTag();
            QName elementName = reader.getName();
            impl = loader.load(reader, Implementation.class, context);
            if (impl == null || impl.getComponentType() == null) {
                // error loading impl
                return componentDefinition;
            }
            // TODO when the loader registry is replaced this try..catch must be replaced with a check for a loader and an
            // UnrecognizedElement added to the context if none is found
            if (!reader.getName().equals(elementName) || reader.getEventType() != END_ELEMENT) {
                // ensure that the implementation loader has positioned the cursor to the end element 
                throw new AssertionError("Impementation loader must position the cursor to the end element");
            }
        } catch (UnrecognizedElementException e) {
            UnrecognizedElement failure = new UnrecognizedElement(reader);
            context.addError(failure);
            return null;
        }
        componentDefinition.setImplementation(impl);
        AbstractComponentType<?, ?, ?, ?> componentType = impl.getComponentType();

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                QName qname = reader.getName();
                if (PROPERTY.equals(qname)) {
                    parseProperty(componentDefinition, componentType, reader, context);
                } else if (REFERENCE.equals(qname)) {
                    parseReference(componentDefinition, componentType, reader, context);
                } else if (SERVICE.equals(qname)) {
                    parseService(componentDefinition, componentType, reader, context);
                } else {
                    // Unknown extension element - issue an error and continue
                    context.addError(new UnrecognizedElement(reader));
                    LoaderUtil.skipToEndElement(reader);
                }
                break;
            case END_ELEMENT:
                assert COMPONENT.equals(reader.getName());
                validateRequiredProperties(componentDefinition, reader, context);
                return componentDefinition;
            }
        }
    }

    private void parseService(ComponentDefinition<Implementation<?>> componentDefinition,
                              AbstractComponentType<?, ?, ?, ?> componentType,
                              XMLStreamReader reader,
                              IntrospectionContext context) throws XMLStreamException {
        ComponentService service;
        service = serviceLoader.load(reader, context);
        if (service == null) {
            // there was an error with the service configuration, just skip it
            return;
        }
        if (!componentType.hasService(service.getName())) {
            // ensure the service exists
            ComponentServiceNotFound failure = new ComponentServiceNotFound(service.getName(), componentDefinition, reader);
            context.addError(failure);
            return;
        }
        if (componentDefinition.getServices().containsKey(service.getName())) {
            String id = service.getName();
            DuplicateComponentService failure = new DuplicateComponentService(id, componentDefinition.getName(), reader);
            context.addError(failure);
        } else {
            componentDefinition.add(service);
        }
    }

    private void parseReference(ComponentDefinition<Implementation<?>> componentDefinition,
                                AbstractComponentType<?, ?, ?, ?> componentType,
                                XMLStreamReader reader,
                                IntrospectionContext context) throws XMLStreamException {
        ComponentReference reference;
        reference = referenceLoader.load(reader, context);
        if (reference == null) {
            // there was an error with the reference configuration, just skip it
            return;
        }
        if (!componentType.hasReference(reference.getName())) {
            // ensure the reference exists
            ComponentReferenceNotFound failure = new ComponentReferenceNotFound(reference.getName(), componentDefinition, reader);
            context.addError(failure);
            return;
        }
        String refKey = reference.getName();
        if (componentDefinition.getReferences().containsKey(refKey)) {
            DuplicateComponentReference failure = new DuplicateComponentReference(refKey, componentDefinition.getName(), reader);
            context.addError(failure);
        } else {
            componentDefinition.add(reference);
        }
    }

    private void parseProperty(ComponentDefinition<Implementation<?>> componentDefinition,
                               AbstractComponentType<?, ?, ?, ?> componentType,
                               XMLStreamReader reader,
                               IntrospectionContext context) throws XMLStreamException {
        PropertyValue value;
        value = propertyValueLoader.load(reader, context);
        if (value == null) {
            // there was an error with the property configuration, just skip it
            return;
        }
        if (!componentType.hasProperty(value.getName())) {
            // ensure the property exists
            ComponentPropertyNotFound failure = new ComponentPropertyNotFound(value.getName(), componentDefinition, reader);
            context.addError(failure);
            return;
        }
        if (componentDefinition.getPropertyValues().containsKey(value.getName())) {
            String id = value.getName();
            DuplicateConfiguredProperty failure = new DuplicateConfiguredProperty(id, componentDefinition, reader);
            context.addError(failure);
        } else {
            componentDefinition.add(value);
        }
    }

    private void validateRequiredProperties(ComponentDefinition<? extends Implementation<?>> definition,
                                            XMLStreamReader reader,
                                            IntrospectionContext context) {
        AbstractComponentType<?, ?, ?, ?> type = definition.getImplementation().getComponentType();
        Map<String, ? extends Property> properties = type.getProperties();
        Map<String, PropertyValue> values = definition.getPropertyValues();
        for (Property property : properties.values()) {
            if (property.isRequired() && !values.containsKey(property.getName())) {
                RequiredPropertyNotProvided failure = new RequiredPropertyNotProvided(property, definition.getName(), reader);
                context.addError(failure);
            }
        }
    }

    /**
     * Loads the key when the component is wired to a map based reference.
     *
     * @param componentDefinition the component definition
     * @param reader              a reader positioned on the element containing the key definition @return a Document containing the key value.
     */
    private void loadKey(ComponentDefinition<Implementation<?>> componentDefinition, XMLStreamReader reader) {
    	componentDefinition.setKey(loaderHelper.loadKey(reader));
    }

    /*
     * Loads the init level.
     */
    private void loadInitLevel(ComponentDefinition<Implementation<?>> componentDefinition, XMLStreamReader reader, IntrospectionContext context) {
        String initLevel = reader.getAttributeValue(null, "initLevel");
        if (initLevel != null && initLevel.length() != 0) {
            try {
                Integer level = Integer.valueOf(initLevel);
                componentDefinition.setInitLevel(level);
            } catch (NumberFormatException e) {
                InvalidValue failure = new InvalidValue("Component initialization level must be an integer: " + initLevel, initLevel, reader);
                context.addError(failure);
            }
        }
    }

    /*
     * Loads the runtime id.
     */
    private void loadRuntimeId(ComponentDefinition<Implementation<?>> componentDefinition, XMLStreamReader reader, IntrospectionContext context) {
        String runtimeAttr = reader.getAttributeValue(null, "runtimeId");
        if (runtimeAttr != null) {
            try {
                componentDefinition.setRuntimeId(new URI(runtimeAttr));
            } catch (URISyntaxException e) {
                InvalidValue failure = new InvalidValue("Component runtime ID must be a valid URI: " + runtimeAttr, runtimeAttr, reader);
                context.addError(failure);
            }
        }
    }

    /*
     * Loads the promoted attribute.
     */
    private void loadPromoted(ComponentDefinition<Implementation<?>> componentDefinition, XMLStreamReader reader) {
        String promoted = reader.getAttributeValue(Namespaces.SCA4J_NS, "promoted");
        if (promoted != null) {
            componentDefinition.setPromoted(Boolean.valueOf(promoted));
        }
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!ATTRIBUTES.containsKey(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}

