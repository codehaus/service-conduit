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
package org.sca4j.bpel.introspection;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

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
import org.sca4j.bpel.model.BpelComponentType;
import org.sca4j.bpel.model.BpelComponentTypeResourceElement;
import org.sca4j.bpel.model.PartnerLink;
import org.sca4j.bpel.model.PartnerLinkType;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.idl.wsdl.model.PortTypeResourceElement;
import org.sca4j.idl.wsdl.model.WsdlContract;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceProcessor;

public class BpelResourceProcessor implements ResourceProcessor {
    
    @Reference public XMLFactory xmlFactory;
    @Reference public MetaDataStore metaDataStore;

    @Override
    public void index(Contribution contribution, URL url, ValidationContext context) throws ContributionException {
        
        Resource resource = new Resource(url);
        contribution.addResource(resource);
        
        InputStream inputStream = null;
        try {

            inputStream = resource.getUrl().openStream();
            XMLStreamReader reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(inputStream);
            
            while (true) {
                switch (reader.next()) {
                case START_ELEMENT:
                    QName name = reader.getName();
                    if (name.equals(Constants.PROCESS_ELEMENT)) {
                        QName processName = new QName(reader.getAttributeValue(null, "targetNamespace"), reader.getAttributeValue(null, "name"));
                        BpelComponentType componentType = new BpelComponentType(resource.getUrl(), processName);
                        BpelComponentTypeResourceElement resourceElement = new BpelComponentTypeResourceElement(componentType);
                        resource.addResourceElement(resourceElement);
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

    @Override
    public void process(URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader) throws ContributionException {
        
        if (resource.isProcessed()) {
            return;
        }
        
        InputStream inputStream = null;
        try {

            inputStream = resource.getUrl().openStream();
            XMLStreamReader reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(inputStream);
            
            BpelComponentType componentType = resource.getResourceElements(BpelComponentTypeResourceElement.class).iterator().next().getComponentType();
            Map<QName, PartnerLink> partnerLinks = new HashMap<QName, PartnerLink>();
            
            while (true) {
                switch (reader.next()) {
                case START_ELEMENT:
                    QName name = reader.getName();
                    if (name.equals(Constants.PARTNERLINK_ELEMENT)) {
                        PartnerLink partnerLink = processPartnerLink(reader, componentType.getProcessName().getNamespaceURI());
                        partnerLinks.put(partnerLink.getName(), partnerLink);
                    } else if (name.equals(Constants.RECEIVE_ELEMENT) || name.equals(Constants.INVOKE_ELEMENT)) {
                        processActivity(contributionUri, reader, componentType, partnerLinks);
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
     * Interprets the receive activity as a service and invoke activity as a reference.
     */
    private void processActivity(URI contributionUri, XMLStreamReader reader, BpelComponentType componentType, Map<QName, PartnerLink> partnerLinks) throws MetaDataStoreException {

        ValidationContext validationContext = new DefaultValidationContext();
        boolean service = Constants.RECEIVE_ELEMENT.equals(reader.getName());
        
        QName partnerLinkName = new QName(componentType.getProcessName().getNamespaceURI(), reader.getAttributeValue(null, "partnerLink"));
        PartnerLink partnerLink = partnerLinks.get(partnerLinkName);
        if (partnerLink == null) {
            throw new MetaDataStoreException("Specified partner link not found in the process " + partnerLinkName);
        }
        QName partnerLinkTypeName = partnerLink.getType();
        String role = service ? partnerLink.getMyRole() : partnerLink.getPartnerRole();
        
        PartnerLinkType partnerLinkType = metaDataStore.resolve(contributionUri, PartnerLinkType.class, partnerLinkTypeName, validationContext);
        if (partnerLinkType == null) {
            throw new MetaDataStoreException("Specified partner link type not resolved " + partnerLinkTypeName);
        }
        QName portTypeName = partnerLinkType.getPortTypes().get(role);
        
        componentType.getPortTypeToPartnerLinks().put(portTypeName, partnerLinkName.getLocalPart());
        
        PortTypeResourceElement portTypeResourceElement = metaDataStore.resolve(contributionUri, PortTypeResourceElement.class, portTypeName, validationContext);
        if (partnerLinkType == null) {
            throw new MetaDataStoreException("Specified port type not resolved " + portTypeName);
        }
        WsdlContract wsdlContract = new WsdlContract();
        wsdlContract.setPortTypeName(portTypeName);
        wsdlContract.setOperations(portTypeResourceElement.getOperations());
        
        if (service) {
            ServiceDefinition serviceDefinition = new ServiceDefinition(partnerLink.getName().getLocalPart(), wsdlContract);
            componentType.add(serviceDefinition);
        } else {
            ReferenceDefinition referenceDefinition = new ReferenceDefinition(partnerLink.getName().getLocalPart(), wsdlContract);
            componentType.add(referenceDefinition);
        }
        
        componentType.getPortTypes().put(portTypeName, portTypeResourceElement.getDefinition());
        
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
