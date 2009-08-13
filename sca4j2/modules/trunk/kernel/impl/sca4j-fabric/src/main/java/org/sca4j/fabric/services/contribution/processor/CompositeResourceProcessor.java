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
package org.sca4j.fabric.services.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

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
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;
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
    private Loader loader;
    private final XMLInputFactory xmlFactory;

    public CompositeResourceProcessor(@Reference ProcessorRegistry processorRegistry,
                                      @Reference Loader loader,
                                      @Reference XMLFactory xmlFactory) {
        processorRegistry.register(this);
        this.loader = loader;
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
            Resource resource = new Resource(url, Constants.COMPOSITE_CONTENT_TYPE);
            String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
            QName compositeName = new QName(targetNamespace, name);
            QNameSymbol symbol = new QNameSymbol(compositeName);
            ResourceElement<QNameSymbol, Composite> element = new ResourceElement<QNameSymbol, Composite>(symbol);
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
        for (ResourceElement element : resource.getResourceElements()) {
            if (element.getSymbol().getKey().equals(composite.getName())) {
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
