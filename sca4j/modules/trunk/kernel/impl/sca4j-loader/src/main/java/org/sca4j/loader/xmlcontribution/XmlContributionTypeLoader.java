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
package org.sca4j.loader.xmlcontribution;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.Constants;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.Deployable;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.ValidationContext;

import static org.sca4j.host.Namespaces.SCA4J_NS;

import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.contribution.ResourceElementNotFoundException;
import org.sca4j.spi.services.contribution.Symbol;
import org.sca4j.spi.services.contribution.XmlProcessor;
import org.sca4j.spi.services.contribution.XmlProcessorRegistry;

/**
 * Loader for definitions.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class XmlContributionTypeLoader implements XmlProcessor {
    private static final QName XML_CONTRIBUTION = new QName(SCA4J_NS, "xmlContribution");
    static final QName COMPOSITE = new QName(SCA_NS, "composite");

    private XmlProcessorRegistry processorRegistry;
    private Loader loader;

    public XmlContributionTypeLoader(@Reference XmlProcessorRegistry processorRegistry, @Reference Loader loader) {
        this.processorRegistry = processorRegistry;
        this.loader = loader;
    }

    @Init
    public void init() {
        processorRegistry.register(this);
    }

    public QName getType() {
        return XML_CONTRIBUTION;
    }

    public void processContent(Contribution contribution, ValidationContext context, XMLStreamReader reader, ClassLoader classLoader)
            throws ContributionException {
        validateAttributes(reader, context);
        List<Composite> composites = new ArrayList<Composite>();
        String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
        URI contributionUri = contribution.getUri();
        try {
            IntrospectionContext childContext = new DefaultIntrospectionContext(contributionUri, classLoader, targetNamespace);
            while (true) {
                switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    Composite definition = null;
                    if (COMPOSITE.equals(qname)) {
                        try {
                            definition = loader.load(reader, Composite.class, childContext);
                        } catch (UnrecognizedElementException e) {
                            throw new ContributionException("Error processing contribution: " + contributionUri.toString(), e);
                        }
                    }
                    if (definition != null) {
                        composites.add(definition);
                    }
                    break;
                case END_ELEMENT:
                    QName name = reader.getName();
                    if (XML_CONTRIBUTION.equals(name)) {
                        for (Composite composite : composites) {
                            boolean found = false;
                            Symbol candidateSymbol = new QNameSymbol(composite.getName());
                            for (Resource resource : contribution.getResources()) {
                                for (ResourceElement element : resource.getResourceElements()) {
                                    if (element.getSymbol().equals(candidateSymbol)) {
                                        element.setValue(composite);
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) {
                                    break;
                                }
                            }
                            if (!found) {
                                String id = composite.getName().toString();
                                // xcv should this be thrown?
                                throw new ResourceElementNotFoundException("Composite not found: " + id, id);
                            }
                        }
                        ContributionManifest manifest = contribution.getManifest();
                        // if no deployables are specified, assume all composites are
                        if (manifest.getDeployables().isEmpty()) {
                            for (Composite composite : composites) {
                                Deployable deployable = new Deployable(composite.getName(), Constants.COMPOSITE_TYPE);
                                manifest.addDeployable(deployable);
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
                    // update indexed elements with the loaded definitions
                }
            }
        } catch (XMLStreamException e) {
            String uri = contribution.getUri().toString();
            throw new ContributionException("Error processing contribution: " + uri, e);
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
