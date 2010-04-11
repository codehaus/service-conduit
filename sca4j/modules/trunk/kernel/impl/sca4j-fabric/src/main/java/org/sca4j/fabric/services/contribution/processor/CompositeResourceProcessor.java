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
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.host.contribution.Constants;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.spi.services.contribution.CompositeResourceElement;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElementNotFoundException;
import org.sca4j.spi.services.contribution.ResourceProcessor;

/**
 * Introspects a composite SCDL file in a contribution and produces a Composite type. This implementation assumes the CCL has all necessary artifacts
 * to perform introspection on its classpath.
 *
 * @version $Rev: 4352 $ $Date: 2008-05-25 22:51:33 +0100 (Sun, 25 May 2008) $
 */
@EagerInit
public class CompositeResourceProcessor implements ResourceProcessor {
    
    @Reference public Loader loader;
    private final XMLInputFactory xmlFactory;

    public CompositeResourceProcessor(@Reference XMLFactory xmlFactory) {
        this.xmlFactory = xmlFactory.newInputFactoryInstance();
    }

    public String getContentType() {
        return Constants.COMPOSITE_CONTENT_TYPE;
    }

    public void index(Contribution contribution, URL url, ValidationContext context) throws ContributionException {
        XMLStreamReader reader = null;
        InputStream stream = null;
        try {
            stream = url.openStream();
            reader = xmlFactory.createXMLStreamReader(stream);
            reader.nextTag();
            String name = reader.getAttributeValue(null, "name");
            if (name == null) {
                context.addError(new MissingAttribute("Composite name not specified", "name", reader));
                return;
            }
            Resource resource = new Resource(url);
            String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
            QName compositeName = new QName(targetNamespace, name);
            CompositeResourceElement element = new CompositeResourceElement(compositeName);
            resource.addResourceElement(element);
            contribution.addResource(resource);
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
        } catch (IOException e) {
            throw new ContributionException(e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public void process(URI contributionUri, Resource resource, ValidationContext context, ClassLoader classLoader) throws ContributionException {
        URL url = resource.getUrl();
        IntrospectionContext childContext = new DefaultIntrospectionContext(classLoader, contributionUri, url);
        Composite composite;
        try {
            // check to see if the resoruce has already been evaluated
            composite = loader.load(url, Composite.class, childContext);
        } catch (LoaderException e) {
            throw new ContributionException(e);
        }
        composite.validate(childContext);
        boolean found = false;
        for (CompositeResourceElement element : resource.getResourceElements(CompositeResourceElement.class)) {
            if (element.getSymbol().equals(composite.getName())) {
                element.setValue(composite);
                found = true;
                break;
            }
        }
        if (!found) {
            String identifier = composite.getName().toString();
            throw new ResourceElementNotFoundException("Resource element not found: " + identifier, identifier);
        }
        if (childContext.hasErrors()) {
            context.addErrors(childContext.getErrors());
        }
        if (childContext.hasWarnings()) {
            context.addWarnings(childContext.getWarnings());
        }
        resource.setProcessed(true);

    }


}
