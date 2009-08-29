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
package org.sca4j.loader.xmlcontribution;

import java.io.FileNotFoundException;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.Deployable;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.scdl.ValidationContext;

import static org.sca4j.host.Namespaces.SCA4J_NS;

import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.Export;
import org.sca4j.spi.services.contribution.Import;
import org.sca4j.spi.services.contribution.XmlElementManifestProcessor;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class XmlContributionTypeManifestProcessor implements XmlElementManifestProcessor {
    private static final QName XML_CONTRIBUTION = new QName(SCA4J_NS, "xmlContribution");
    private static final QName SCA_CONTRIBUTION = new QName(SCA_NS, "contribution");
    private XmlManifestProcessorRegistry manifestProcessorRegistry;
    private Loader loader;

    public XmlContributionTypeManifestProcessor(@Reference XmlManifestProcessorRegistry manifestProcessorRegistry, @Reference Loader loader) {
        this.manifestProcessorRegistry = manifestProcessorRegistry;
        this.loader = loader;
    }

    @Init
    public void init() {
        manifestProcessorRegistry.register(this);
    }

    public QName getType() {
        return XML_CONTRIBUTION;
    }

    public void process(ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException {
        try {
            while (true) {
                int i = reader.next();
                switch (i) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (SCA_CONTRIBUTION.equals(qname)) {
                        ClassLoader cl = getClass().getClassLoader();
                        IntrospectionContext childContext = new DefaultIntrospectionContext(cl, null, null);
                        ContributionManifest embeddedManifest = loader.load(reader, ContributionManifest.class, childContext);
                        if (childContext.hasErrors()) {
                            context.addErrors(childContext.getErrors());
                        }
                        if (childContext.hasWarnings()) {
                            context.addWarnings(childContext.getWarnings());
                        }

                        // merge the contents
                        for (Deployable deployable : embeddedManifest.getDeployables()) {
                            manifest.addDeployable(deployable);
                        }
                        for (Export export : embeddedManifest.getExports()) {
                            manifest.addExport(export);
                        }
                        for (Import imprt : embeddedManifest.getImports()) {
                            manifest.addImport(imprt);
                        }
                    }
                    break;
                case END_ELEMENT:
                    if (SCA_CONTRIBUTION.equals(reader.getName())) {
                        // if we reached here, version was never specified and there are no dependencies
                        return;
                    }
                    break;
                case END_DOCUMENT:
                    return;
                }

            }

        } catch (LoaderException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                return;
            } else {
                throw new ContributionException(e);
            }
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
        }

    }


}
