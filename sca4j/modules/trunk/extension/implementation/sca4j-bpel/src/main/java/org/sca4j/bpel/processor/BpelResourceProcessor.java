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
 */
package org.sca4j.bpel.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.model.BpelProcessDefinition;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceProcessor;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public class BpelResourceProcessor implements ResourceProcessor {
    
    @Reference public XMLFactory xmlFactory;

    @Override
    public void index(Contribution contribution, URL url, ValidationContext context) throws ContributionException {
        Resource resource = new Resource(url);
        contribution.addResource(resource);
    }

    @Override
    public void process(URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader) throws ContributionException {
        
        if (resource.isProcessed()) {
            return;
        }
        
        InputStream inputStream = null;
        
        try {
            inputStream = resource.getUrl().openStream();
            URL processUrl = null;
            QName processName = null;
            Map<String, QName> services = new HashMap<String, QName>();
            Map<String, QName> references = new HashMap<String, QName>();
            Map<String, QName> properties = new HashMap<String, QName>();
            XMLStreamReader reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(inputStream);
            
            while (true) {
                switch (reader.next()) {
                case START_ELEMENT:
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(Constants.PROCESS_ELEMENT_20)) {
                        BpelProcessDefinition def = new BpelProcessDefinition(processUrl, processName, services, references, properties);
                        return;
                    }
                    break;
                }
            }
        } catch (XMLFactoryInstantiationException e) {
            throw new ContributionException(e);
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
        } catch (IOException e) {
            throw new ContributionException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
    }

}
