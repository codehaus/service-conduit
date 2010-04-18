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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.EagerInit;
import org.sca4j.bpel.model.PartnerLinkTypeExtension;
import org.sca4j.idl.wsdl.processor.extension.ExtensionHandler;
import org.sca4j.spi.services.contribution.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Handles partner link type extension in the WSDL.
 * 
 * @author meerajk
 *
 */
@EagerInit
public class PartnerLinkTypeExtensionHandler implements ExtensionHandler<PartnerLinkTypeExtension> {
    
    private static final QName PARTNER_LINK_TYPE = new QName("http://docs.oasis-open.org/wsbpel/2.0/plnktype", "partnerLinkType");

    @Override
    public QName getQualifiedName() {
        return PARTNER_LINK_TYPE;
    }

    @Override
    public void onExtension(PartnerLinkTypeExtension extensibilityElement, Resource resource) {
        resource.addResourceElement(extensibilityElement);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void marshall(Class theClass, QName qName, ExtensibilityElement extension, PrintWriter writer, Definition definition, ExtensionRegistry registry) throws WSDLException {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ExtensibilityElement unmarshall(Class theClass, QName qName, Element element, Definition definition, ExtensionRegistry registry) throws WSDLException {
        
        String targetNamespace = definition.getTargetNamespace();
        String name = element.getAttribute("name");
        if (name == null) {
            throw new WSDLException(null, "Name attribute not defined");
        }
        QName partnerLinkName = new QName(targetNamespace, name);
        
        Map<String, QName> portTypes = new HashMap<String, QName>();
        NodeList roles = element.getElementsByTagNameNS(PARTNER_LINK_TYPE.getNamespaceURI(), "role");
        for ( int i=0; i < roles.getLength(); i++ ) {
            Element roleNode = (Element) roles.item(i);
            String roleName = roleNode.getAttribute("name");
            QName portType = getPortTypeName(definition, roleNode.getAttribute("portType"));
            if (definition.getPortType(portType) == null) {
                throw new WSDLException(null, "Port type not defined " + portType.toString());
            }
            portTypes.put(roleName, portType);
        }
        
        return new PartnerLinkTypeExtension(partnerLinkName, portTypes);
        
    }
    
    private QName getPortTypeName(Definition definition, String value) {
        if (value != null && definition != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = definition.getNamespace(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

}
