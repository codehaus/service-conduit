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

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.introspection.xml.UnrecognizedElement;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.scdl.ArtifactValidationFailure;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeReference;
import org.sca4j.scdl.CompositeService;
import org.sca4j.scdl.Include;
import org.sca4j.scdl.ModelObject;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.WireDefinition;

/**
 * Loads a composite component definition from an XML-based assembly file
 *
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
@EagerInit
public class CompositeLoader implements TypeLoader<Composite> {
    public static final QName COMPOSITE = new QName(SCA_NS, "composite");
    public static final QName INCLUDE = new QName(SCA_NS, "include");
    public static final QName PROPERTY = new QName(SCA_NS, "property");
    public static final QName SERVICE = new QName(SCA_NS, "service");
    public static final QName REFERENCE = new QName(SCA_NS, "reference");
    public static final QName COMPONENT = new QName(SCA_NS, "component");
    public static final QName WIRE = new QName(SCA_NS, "wire");

    private static final Set<String> ATTRIBUTES = new HashSet<String>();

    static {
        ATTRIBUTES.add("name");
        ATTRIBUTES.add("autowire");
        ATTRIBUTES.add("targetNamespace");
        ATTRIBUTES.add("local");
        ATTRIBUTES.add("requires");
        ATTRIBUTES.add("policySets");
        ATTRIBUTES.add("constrainingType");
    }

    private final LoaderRegistry registry;
    private final Loader loader;
    private final TypeLoader<Include> includeLoader;
    private final TypeLoader<Property> propertyLoader;
    private final TypeLoader<CompositeService> serviceLoader;
    private final TypeLoader<CompositeReference> referenceLoader;
    private final TypeLoader<ComponentDefinition<?>> componentLoader;
    private final TypeLoader<WireDefinition> wireLoader;
    private final LoaderHelper loaderHelper;

    /**
     * Constructor used during bootstrap.
     *
     * @param loader          loader for extension elements
     * @param includeLoader   loader for include elements
     * @param propertyLoader  loader for composite property elements
     * @param componentLoader loader for component elements
     * @param wireLoader      loader for wire elements
     * @param loaderHelper    helper
     */
    public CompositeLoader(Loader loader,
                           TypeLoader<Include> includeLoader,
                           TypeLoader<Property> propertyLoader,
                           TypeLoader<ComponentDefinition<?>> componentLoader,
                           TypeLoader<WireDefinition> wireLoader,
                           LoaderHelper loaderHelper) {
        this.loader = loader;
        this.includeLoader = includeLoader;
        this.propertyLoader = propertyLoader;
        this.componentLoader = componentLoader;
        this.wireLoader = wireLoader;
        this.loaderHelper = loaderHelper;

        this.registry = null;
        this.serviceLoader = null;
        this.referenceLoader = null;
    }

    /**
     * Constructor to be used when registering this component through SCDL.
     *
     * @param registry        the loader registry to register with; also used to load extension elements
     * @param includeLoader   loader for include elements
     * @param propertyLoader  loader for composite property elements
     * @param serviceLoader   loader for composite services
     * @param referenceLoader loader for composite references
     * @param componentLoader loader for component elements
     * @param wireLoader      loader for wire elements
     * @param loaderHelper    helper
     */
    @Constructor
    public CompositeLoader(@Reference LoaderRegistry registry,
                           @Reference(name = "include")TypeLoader<Include> includeLoader,
                           @Reference(name = "property")TypeLoader<Property> propertyLoader,
                           @Reference(name = "service")TypeLoader<CompositeService> serviceLoader,
                           @Reference(name = "reference")TypeLoader<CompositeReference> referenceLoader,
                           @Reference(name = "component")TypeLoader<ComponentDefinition<?>> componentLoader,
                           @Reference(name = "wire")TypeLoader<WireDefinition> wireLoader,
                           @Reference(name = "loaderHelper")LoaderHelper loaderHelper) {
        this.registry = registry;
        this.loader = registry;
        this.includeLoader = includeLoader;
        this.propertyLoader = propertyLoader;
        this.serviceLoader = serviceLoader;
        this.referenceLoader = referenceLoader;
        this.componentLoader = componentLoader;
        this.wireLoader = wireLoader;
        this.loaderHelper = loaderHelper;
    }

    public QName getXMLType() {
        return COMPOSITE;
    }

    @Init
    public void init() {
        registry.registerLoader(COMPOSITE, this);
    }

    @Destroy
    public void destroy() {
        registry.unregisterLoader(COMPOSITE);
    }

    public Composite load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        
        validateAttributes(reader, introspectionContext);
        String name = reader.getAttributeValue(null, "name");
        String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
        boolean local = Boolean.valueOf(reader.getAttributeValue(null, "local"));
        IntrospectionContext childContext = new DefaultIntrospectionContext(introspectionContext, targetNamespace);
        QName compositeName = new QName(targetNamespace, name);
        NamespaceContext namespace = reader.getNamespaceContext();
        String constrainingTypeAttrbute = reader.getAttributeValue(null, "constrainingType");
        QName constrainingType = LoaderUtil.getQName(constrainingTypeAttrbute, targetNamespace, namespace);
        
        Composite type = new Composite(compositeName);
        type.setLocal(local);
        type.setAutowire(Autowire.fromString(reader.getAttributeValue(null, "autowire")));
        type.setConstrainingType(constrainingType);
        loaderHelper.loadPolicySetsAndIntents(type, reader, childContext);
        
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                QName qname = reader.getName();
                if (INCLUDE.equals(qname)) {
                    Include include = includeLoader.load(reader, childContext);
                    if (include == null) {
                        // errror encountered loading the include
                        continue;
                    }
                    QName includeName = include.getName();
                    if (type.getIncludes().containsKey(includeName)) {
                        String identifier = includeName.toString();
                        DuplicateInclude failure = new DuplicateInclude(identifier, reader);
                        childContext.addError(failure);
                        continue;
                    }
                    if (childContext.hasErrors()) {
                        continue;
                    }
                    for (ComponentDefinition definition : include.getIncluded().getComponents().values()) {
                        String key = definition.getName();
                        if (type.getComponents().containsKey(key)) {
                            DuplicateComponentName failure = new DuplicateComponentName(key, reader);
                            childContext.addError(failure);
                        } else {
                            type.add(definition);
                        }
                    }
                    type.add(include);
                    // TODO do the same for services,references, wires and properties
                } else if (PROPERTY.equals(qname)) {
                    Property property = propertyLoader.load(reader, childContext);
                    if (property == null) {
                        // errror encountered loading the property
                        continue;
                    }
                    String key = property.getName();
                    if (type.getProperties().containsKey(key)) {
                        DuplicateProperty failure = new DuplicateProperty(key, reader);
                        childContext.addError(failure);
                    } else {
                        type.add(property);
                    }
                } else if (SERVICE.equals(qname)) {
                    CompositeService service = serviceLoader.load(reader, childContext);
                    if (service == null) {
                        // errror encountered loading the service
                        continue;
                    }
                    if (type.getServices().containsKey(service.getName())) {
                        String key = service.getName();
                        DuplicateService failure = new DuplicateService(key, reader);
                        childContext.addError(failure);
                    } else {
                        type.add(service);
                    }
                } else if (REFERENCE.equals(qname)) {
                    CompositeReference reference = referenceLoader.load(reader, childContext);
                    if (reference == null) {
                        // errror encountered loading the reference
                        continue;
                    }
                    if (type.getReferences().containsKey(reference.getName())) {
                        String key = reference.getName();
                        DuplicatePromotedReferenceName failure = new DuplicatePromotedReferenceName(key, reader);
                        childContext.addError(failure);
                    } else {
                        type.add(reference);
                    }
                } else if (COMPONENT.equals(qname)) {
                    ComponentDefinition<?> componentDefinition = componentLoader.load(reader, childContext);
                    if (componentDefinition == null) {
                        // errror encountered loading the componentDefinition
                        continue;
                    }
                    String key = componentDefinition.getName();
                    if (type.getComponents().containsKey(key)) {
                        DuplicateComponentName failure = new DuplicateComponentName(key, reader);
                        childContext.addError(failure);
                        continue;
                    }
                    if (type.getAutowire() != Autowire.INHERITED && componentDefinition.getAutowire() == Autowire.INHERITED) {
                        componentDefinition.setAutowire(type.getAutowire());
                    }
                    type.add(componentDefinition);
                } else if (WIRE.equals(qname)) {
                    WireDefinition wire = wireLoader.load(reader, childContext);
                    if (wire == null) {
                        // errror encountered loading the wire
                        continue;
                    }
                    type.add(wire);
                } else {
                    // Extension element - for now try to load and see if we can handle it
                    ModelObject modelObject;
                    try {
                        modelObject = loader.load(reader, ModelObject.class, childContext);
                        // TODO when the loader registry is replaced this try..catch must be replaced with a check for a loader and an
                        // UnrecognizedElement added to the context if none is found
                    } catch (UnrecognizedElementException e) {
                        UnrecognizedElement failure = new UnrecognizedElement(reader);
                        childContext.addError(failure);
                        continue;
                    }
                    if (modelObject instanceof Property) {
                        type.add((Property) modelObject);
                    } else if (modelObject instanceof CompositeService) {
                        type.add((CompositeService) modelObject);
                    } else if (modelObject instanceof CompositeReference) {
                        type.add((CompositeReference) modelObject);
                    } else if (modelObject instanceof ComponentDefinition) {
                        type.add((ComponentDefinition<?>) modelObject);
                    } else if (type == null) {
                        // there was an error loading the element, ingore it as the errors will have been reported
                        continue;
                    } else {
                        childContext.addError(new UnrecognizedElement(reader));
                        continue;
                    }
                }
                break;
            case END_ELEMENT:
                assert COMPOSITE.equals(reader.getName());
                if (childContext.hasErrors() || childContext.hasWarnings()) {
                    if (childContext.hasErrors()) {
                        ArtifactValidationFailure artifactFailure = new ArtifactValidationFailure(compositeName.toString());
                        artifactFailure.addFailures(childContext.getErrors());
                        introspectionContext.addError(artifactFailure);
                    }
                    if (childContext.hasWarnings()) {
                        ArtifactValidationFailure artifactFailure = new ArtifactValidationFailure(compositeName.toString());
                        artifactFailure.addFailures(childContext.getWarnings());
                        introspectionContext.addWarning(artifactFailure);
                    }
                }
                return type;
            }
        }
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!ATTRIBUTES.contains(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
