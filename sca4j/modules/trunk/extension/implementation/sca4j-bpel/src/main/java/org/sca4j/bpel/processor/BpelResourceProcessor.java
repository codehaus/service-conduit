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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.model.BpelProcessDefinition;
import org.sca4j.bpel.model.InvokeActivity;
import org.sca4j.bpel.model.PartnerLink;
import org.sca4j.bpel.model.ReceiveActivity;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceProcessor;

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
            
            XMLStreamReader reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(inputStream);
            inputStream = resource.getUrl().openStream();
            BpelProcessDefinition processDefinition = null;
            
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName name = reader.getName();
                        if (name.equals(Constants.PROCESS_ELEMENT)) {
                            QName processName = new QName(reader.getAttributeValue(null, "targetNamespace"), reader.getAttributeValue(null, "name"));
                            processDefinition = new BpelProcessDefinition(resource.getUrl(), processName);
                            resource.addResourceElement(processDefinition);
                        } else if (name.equals(Constants.PARTNERLINK_ELEMENT)) {
                            PartnerLink partnerLink = processPartnerLink(reader, processDefinition.getProcessName().getNamespaceURI());
                            processDefinition.getPartnerLinks().put(partnerLink.getName(), partnerLink);
                        } else if (name.equals(Constants.RECEIVE_ELEMENT)) {
                            ReceiveActivity receiveActivity = processReceiveActivity(reader, processDefinition.getProcessName().getNamespaceURI());
                            processDefinition.getReceiveActivities().add(receiveActivity);
                        } else if (name.equals(Constants.INVOKE_ELEMENT)) {
                            InvokeActivity invokeActivity = processInvokeActivity(reader, processDefinition.getProcessName().getNamespaceURI());
                            processDefinition.getInvokeActivities().add(invokeActivity);
                        }
                        break;
                    case END_ELEMENT:
                        if (reader.getName().equals(Constants.PROCESS_ELEMENT)) {
                            resource.setProcessed(true);
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

    /*
     * Creates an invoke activity.
     */
    private InvokeActivity processInvokeActivity(XMLStreamReader reader, String namespaceURI) {
        QName partnerLink = new QName(namespaceURI, reader.getAttributeValue(null, "partnerLink"));
        String operation = reader.getAttributeValue(null, "operation");
        return new InvokeActivity(partnerLink, operation);
    }

    /*
     * Creates a receive activity.
     */
    private ReceiveActivity processReceiveActivity(XMLStreamReader reader, String namespaceURI) {
        QName partnerLink = new QName(namespaceURI, reader.getAttributeValue(null, "partnerLink"));
        String operation = reader.getAttributeValue(null, "operation");
        return new ReceiveActivity(partnerLink, operation);
    }

    /*
     * Creates a partner link object.
     */
    private PartnerLink processPartnerLink(XMLStreamReader reader, String targetNamespace) {
        QName name = new QName(targetNamespace, reader.getAttributeValue(null, "name"));
        QName type = LoaderUtil.getQName(reader.getAttributeValue(null, "partnerLinkType"), null, reader.getNamespaceContext());
        String myRole = reader.getAttributeValue(null, "myRole");
        String partnerRole = reader.getAttributeValue(null, "partnerRole");
        return new PartnerLink(name, type, myRole, partnerRole);
    }

}
