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
package org.sca4j.fabric.services.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.ContributionProcessor;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.XmlIndexerRegistry;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;
import org.sca4j.spi.services.contribution.XmlProcessorRegistry;
import org.sca4j.scdl.ValidationContext;

/**
 * Processes an XML-based contribution. The implementaton dispatches to a specific XmlProcessor based on the QName of the document element.
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public class XmlContributionProcessor implements ContributionProcessor {
    private static final List<String> CONTENT_TYPES = initializeContentTypes();
    private XMLInputFactory xmlFactory;
    private ProcessorRegistry processorRegistry;
    private XmlManifestProcessorRegistry manifestProcessorRegistry;
    private XmlProcessorRegistry xmlProcessorRegistry;
    private XmlIndexerRegistry xmlIndexerRegistry;

    public XmlContributionProcessor(@Reference ProcessorRegistry processorRegistry,
                                    @Reference XmlManifestProcessorRegistry manifestProcessorRegistry,
                                    @Reference XmlProcessorRegistry xmlProcessorRegistry,
                                    @Reference XmlIndexerRegistry xmlIndexerRegistry,
                                    @Reference XMLFactory xmlFactory) {
        this.processorRegistry = processorRegistry;
        this.manifestProcessorRegistry = manifestProcessorRegistry;
        this.xmlProcessorRegistry = xmlProcessorRegistry;
        this.xmlIndexerRegistry = xmlIndexerRegistry;
        this.xmlFactory = xmlFactory.newInputFactoryInstance();
    }

    @Init
    public void init() {
        processorRegistry.register(this);
    }

    public List<String> getContentTypes() {
        return CONTENT_TYPES;
    }

    public void processManifest(Contribution contribution, ValidationContext context) throws ContributionException {
        ContributionManifest manifest = new ContributionManifest();
        contribution.setManifest(manifest);
        InputStream stream = null;
        XMLStreamReader reader = null;
        try {
            URL locationURL = contribution.getLocation();
            stream = locationURL.openStream();
            reader = xmlFactory.createXMLStreamReader(stream);
            reader.nextTag();
            QName name = reader.getName();
            manifestProcessorRegistry.process(name, manifest, reader, context);
        } catch (IOException e) {
            String uri = contribution.getUri().toString();
            throw new ContributionException("Error processing contribution " + uri, e);
        } catch (XMLStreamException e) {
            String uri = contribution.getUri().toString();
            int line = e.getLocation().getLineNumber();
            int col = e.getLocation().getColumnNumber();
            throw new ContributionException("Error processing contribution " + uri + " [" + line + "," + col + "]", e);
        } finally {
            close(stream, reader);
        }
    }

    public void index(Contribution contribution, ValidationContext context) throws ContributionException {
        InputStream stream = null;
        XMLStreamReader reader = null;
        try {
            URL locationURL = contribution.getLocation();
            stream = locationURL.openStream();
            reader = xmlFactory.createXMLStreamReader(stream);
            reader.nextTag();
            Resource resource = new Resource(contribution.getLocation(), "application/xml");
            xmlIndexerRegistry.index(resource, reader, context);
            contribution.addResource(resource);
        } catch (IOException e) {
            String uri = contribution.getUri().toString();
            throw new ContributionException("Error processing contribution " + uri, e);
        } catch (XMLStreamException e) {
            String uri = contribution.getUri().toString();
            int line = e.getLocation().getLineNumber();
            int col = e.getLocation().getColumnNumber();
            throw new ContributionException("Error processing contribution " + uri + " [" + line + "," + col + "]", e);
        } finally {
            close(stream, reader);
        }
    }

    public void process(Contribution contribution, ValidationContext context, ClassLoader loader) throws ContributionException {
        URL locationURL = contribution.getLocation();
        InputStream stream = null;
        XMLStreamReader reader = null;
        try {
            stream = locationURL.openStream();
            reader = xmlFactory.createXMLStreamReader(stream);
            reader.nextTag();
            xmlProcessorRegistry.process(contribution, reader, context, loader);
        } catch (IOException e) {
            String uri = contribution.getUri().toString();
            throw new ContributionException("Error processing contribution " + uri, e);
        } catch (XMLStreamException e) {
            String uri = contribution.getUri().toString();
            int line = e.getLocation().getLineNumber();
            int col = e.getLocation().getColumnNumber();
            throw new ContributionException("Error processing contribution " + uri + " [" + line + "," + col + "]", e);
        } finally {
            close(stream, reader);
        }
    }

    private void close(InputStream stream, XMLStreamReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> initializeContentTypes() {
        List<String> list = new ArrayList<String>(2);
        list.add("application/xml");
        //list.add("application/vnd.sca4j");
        return list;
    }
}
