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
package org.sca4j.loader.componentType;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedElement;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.scdl.ComponentType;
import org.sca4j.scdl.ModelObject;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;

/**
 * Loads a generic component type.
 *
 * @version $Rev: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
@EagerInit
public class ComponentTypeLoader implements TypeLoader<ComponentType> {
    private static final QName COMPONENT_TYPE = new QName(SCA_NS, "componentType");
    private static final QName PROPERTY = new QName(SCA_NS, "property");
    private static final QName SERVICE = new QName(SCA_NS, "service");
    private static final QName REFERENCE = new QName(SCA_NS, "reference");

    private final LoaderRegistry registry;
    private final TypeLoader<Property> propertyLoader;
    private final TypeLoader<ServiceDefinition> serviceLoader;
    private final TypeLoader<ReferenceDefinition> referenceLoader;

    public ComponentTypeLoader(@Reference LoaderRegistry registry,
                               @Reference(name = "property")TypeLoader<Property> propertyLoader,
                               @Reference(name = "service")TypeLoader<ServiceDefinition> serviceLoader,
                               @Reference(name = "reference")TypeLoader<ReferenceDefinition> referenceLoader
    ) {
        this.registry = registry;
        this.propertyLoader = propertyLoader;
        this.serviceLoader = serviceLoader;
        this.referenceLoader = referenceLoader;
    }

    @Init
    public void init() {
        registry.registerLoader(COMPONENT_TYPE, this);
    }

    @Destroy
    public void destroy() {
        registry.unregisterLoader(COMPONENT_TYPE);
    }

    public QName getXMLType() {
        return COMPONENT_TYPE;
    }

    public ComponentType load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        QName constrainingType = LoaderUtil.getQName(reader.getAttributeValue(null, "constrainingType"),
                                                     introspectionContext.getTargetNamespace(),
                                                     reader.getNamespaceContext());

        ComponentType type = new ComponentType();
        type.setConstrainingType(constrainingType);
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                QName qname = reader.getName();
                if (PROPERTY.equals(qname)) {
                    Property property = propertyLoader.load(reader, introspectionContext);
                    type.add(property);
                } else if (SERVICE.equals(qname)) {
                    ServiceDefinition service = serviceLoader.load(reader, introspectionContext);
                    type.add(service);
                } else if (REFERENCE.equals(qname)) {
                    ReferenceDefinition reference = referenceLoader.load(reader, introspectionContext);
                    type.add(reference);
                } else {
                    // Extension element - for now try to load and see if we can handle it
                    ModelObject modelObject;
                    try {
                        modelObject = registry.load(reader, ModelObject.class, introspectionContext);
                        // TODO when the loader registry is replaced this try..catch must be replaced with a check for a loader and an
                        // UnrecognizedElement added to the context if none is found
                    } catch (UnrecognizedElementException e) {
                        UnrecognizedElement failure = new UnrecognizedElement(reader);
                        introspectionContext.addError(failure);
                        continue;
                    }
                    if (modelObject instanceof Property) {
                        type.add((Property) modelObject);
                    } else if (modelObject instanceof ServiceDefinition) {
                        type.add((ServiceDefinition) modelObject);
                    } else if (modelObject instanceof ReferenceDefinition) {
                        type.add((ReferenceDefinition) modelObject);
                    } else if (type == null) {
                        // error loading, the element, ignore as an error will have been reported
                        break;
                    } else {
                        introspectionContext.addError(new UnrecognizedElement(reader));
                        continue;
                    }
                }
                break;
            case END_ELEMENT:
                return type;
            }
        }
    }
}
