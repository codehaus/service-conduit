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
package org.sca4j.loader.definitions;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.scdl.definitions.AbstractDefinition;
import org.sca4j.scdl.definitions.BindingType;
import org.sca4j.scdl.definitions.ImplementationType;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.contribution.ResourceElementNotFoundException;
import org.sca4j.spi.services.contribution.XmlResourceElementLoader;
import org.sca4j.spi.services.contribution.XmlResourceElementLoaderRegistry;

/**
 * Loader for definitions.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class DefinitionsLoader implements XmlResourceElementLoader {

    static final QName INTENT = new QName(SCA_NS, "intent");
    static final QName DESCRIPTION = new QName(SCA_NS, "description");
    static final QName POLICY_SET = new QName(SCA_NS, "policySet");
    static final QName BINDING_TYPE = new QName(SCA_NS, "bindingType");
    static final QName IMPLEMENTATION_TYPE = new QName(SCA_NS, "implementationType");

    private static final QName DEFINITIONS = new QName(SCA_NS, "definitions");

    private XmlResourceElementLoaderRegistry elementLoaderRegistry;
    private Loader loaderRegistry;

    public DefinitionsLoader(@Reference XmlResourceElementLoaderRegistry elementLoaderRegistry,
                             @Reference Loader loader) {
        this.elementLoaderRegistry = elementLoaderRegistry;
        this.loaderRegistry = loader;
    }

    @Init
    public void init() {
        elementLoaderRegistry.register(this);
    }

    public QName getType() {
        return DEFINITIONS;
    }

    public void load(XMLStreamReader reader, URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader)
            throws ContributionException, XMLStreamException {
        validateAttributes(reader, context);

        List<AbstractDefinition> definitions = new ArrayList<AbstractDefinition>();

        String targetNamespace = reader.getAttributeValue(null, "targetNamespace");

        IntrospectionContext childContext = new DefaultIntrospectionContext(contributionUri, loader, targetNamespace);

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                QName qname = reader.getName();
                AbstractDefinition definition = null;
                if (INTENT.equals(qname)) {
                    try {
                        definition = loaderRegistry.load(reader, Intent.class, childContext);
                    } catch (UnrecognizedElementException e) {
                        throw new ContributionException(e);
                    }
                } else if (POLICY_SET.equals(qname)) {
                    try {
                        definition = loaderRegistry.load(reader, PolicySet.class, childContext);
                    } catch (UnrecognizedElementException e) {
                        throw new ContributionException(e);
                    }
                } else if (BINDING_TYPE.equals(qname)) {
                    try {
                        definition = loaderRegistry.load(reader, BindingType.class, childContext);
                    } catch (UnrecognizedElementException e) {
                        throw new ContributionException(e);
                    }
                } else if (IMPLEMENTATION_TYPE.equals(qname)) {
                    try {
                        definition = loaderRegistry.load(reader, ImplementationType.class, childContext);
                    } catch (UnrecognizedElementException e) {
                        throw new ContributionException(e);
                    }
                }
                if (definition != null) {
                    definitions.add(definition);
                }
                break;
            case END_ELEMENT:
                assert DEFINITIONS.equals(reader.getName());
                if (System.getProperty("sca4j.debug") != null) {
                    for (AbstractDefinition candidate : definitions) {
                        System.err.println("Registered definition " + candidate.getName() + " of type " + candidate.getClass());
                    }
                }
                // update indexed elements with the loaded definitions
                for (AbstractDefinition candidate : definitions) {
                    boolean found = false;
                    for (ResourceElement element : resource.getResourceElements()) {
                        QName candidateSymbol = candidate.getName();
                        if (element.getSymbol().equals(candidateSymbol)) {
                            element.setValue(candidate);
                            found = true;
                        }
                    }
                    if (!found) {
                        String id = candidate.toString();
                        // xcv should not throw?
                        throw new ResourceElementNotFoundException("Definition not found: " + id, id);
                    }
                }
                if (childContext.hasErrors()) {
                    context.addErrors(childContext.getErrors());
                }
                if (childContext.hasWarnings()) {
                    context.addWarnings(childContext.getWarnings());
                }
                return;
            }
        }

    }

    private void validateAttributes(XMLStreamReader reader, ValidationContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!"targetNamespace".equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }


}
