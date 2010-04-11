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
package org.sca4j.fabric.services.contribution.manifest;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.sca4j.host.contribution.Constants;
import org.sca4j.host.contribution.Deployable;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.loader.impl.InvalidQNamePrefix;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.Export;
import org.sca4j.spi.services.contribution.Import;

/**
 * Loads a contribution manifest from a contribution element
 *
 * @version $Rev: 5137 $ $Date: 2008-08-02 08:54:51 +0100 (Sat, 02 Aug 2008) $
 */
@EagerInit
public class ContributionElementLoader implements TypeLoader<ContributionManifest> {
    private static final QName CONTRIBUTION = new QName(SCA_NS, "contribution");
    private static final QName DEPLOYABLE = new QName(SCA_NS, "deployable");

    private final LoaderRegistry registry;
    private final LoaderHelper helper;

    public ContributionElementLoader(@Reference LoaderRegistry registry,
                                     @Reference LoaderHelper helper) {
        this.registry = registry;
        this.helper = helper;
    }

    @Init
    public void start() {
        registry.registerLoader(CONTRIBUTION, this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(CONTRIBUTION);
    }


    public ContributionManifest load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);
        ContributionManifest contribution = new ContributionManifest();
        while (true) {
            int event = reader.next();
            switch (event) {
            case START_ELEMENT:
                QName element = reader.getName();
                if (CONTRIBUTION.equals(element)) {
                    continue;
                } else if (DEPLOYABLE.equals(element)) {
                    String name = reader.getAttributeValue(null, "composite");
                    if (name == null) {
                        MissingMainifestAttribute failure =
                                new MissingMainifestAttribute("Composite attribute must be specified", "composite", reader);
                        context.addError(failure);
                        return null;
                    }
                    QName qName;
                    // read qname but only set namespace if it is explicitly declared
                    int index = name.indexOf(':');
                    if (index != -1) {
                        String prefix = name.substring(0, index);
                        String localPart = name.substring(index + 1);
                        String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
                        if (ns == null) {
                            context.addError(new InvalidQNamePrefix(prefix, reader));
                            return null;
                        }
                        qName = new QName(ns, localPart, prefix);
                    } else {
                        qName = new QName(null, name);
                    }
                    Deployable deployable = new Deployable(qName, Constants.COMPOSITE_TYPE);
                    contribution.addDeployable(deployable);
                } else {
                    Object o = null;
                    try {
                        o = registry.load(reader, Object.class, context);
                    } catch (UnrecognizedElementException e) {
                        //UnrecognizedElement failure = new UnrecognizedElement(reader);
                        //context.addError(failure);
                        //return null;
                    }
                    if (o instanceof Export) {
                        contribution.addExport((Export) o);
                    } else if (o instanceof Import) {
                        contribution.addImport((Import) o);
                    } else if (o != null) {
                        // UnrecognizedElement failure = new UnrecognizedElement(reader);
                        // context.addError(failure);
                        // return null;
                    }
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (CONTRIBUTION.equals(reader.getName())) {
                    return contribution;
                }
                break;
            }
        }
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!"composite".equals(name) && !"extension".equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }


}
