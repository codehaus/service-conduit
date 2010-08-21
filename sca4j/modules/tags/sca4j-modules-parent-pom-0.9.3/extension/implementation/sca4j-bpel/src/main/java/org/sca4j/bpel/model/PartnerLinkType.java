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
package org.sca4j.bpel.model;

import java.util.Map;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.sca4j.spi.services.contribution.ResourceElement;

public class PartnerLinkType extends ResourceElement<QName>implements ExtensibilityElement {
    
    private static final QName PARTNER_LINK_TYPE = new QName("http://docs.oasis-open.org/wsbpel/2.0/plnktype", "partnerLinkType");
    
    
    private Map<String, QName> portTypes;
    
    public PartnerLinkType(QName symbol, Map<String, QName> portTypes) {
        super(symbol);
        this.portTypes = portTypes;
    }

    /**
     * @return the portTypes
     */
    public Map<String, QName> getPortTypes() {
        return portTypes;
    }



    @Override
    public QName getElementType() {
        return PARTNER_LINK_TYPE;
    }

    @Override
    public Boolean getRequired() {
        return Boolean.FALSE;
    }

    @Override
    public void setElementType(QName elementType) {
    }

    @Override
    public void setRequired(Boolean required) {
    }

}
