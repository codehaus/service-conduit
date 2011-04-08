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

import javax.xml.namespace.QName;

/**
 * Represents a BPEL partner link.
 * 
 * @author meerajk
 *
 */
public class PartnerLink {
    
    private QName name;
    private QName type;
    private String myRole;
    private String partnerRole;
    
    /**
     * Initialises the partner link state.
     * 
     * @param name Qualified name of the partner link.
     * @param type Partner link type as defined in the WSDL.
     * @param myRole My role which indicates this could be a service.
     * @param partnerRole Partner role which indicates this could be a reference.
     */
    public PartnerLink(QName name, QName type, String myRole, String partnerRole) {
        this.name = name;
        this.type = type;
        this.myRole = myRole;
        this.partnerRole = partnerRole;
    }

    /**
     * @return the name
     */
    public QName getName() {
        return name;
    }

    /**
     * @return the type
     */
    public QName getType() {
        return type;
    }

    /**
     * @return the myRole
     */
    public String getMyRole() {
        return myRole;
    }

    /**
     * @return the partnerRole
     */
    public String getPartnerRole() {
        return partnerRole;
    }

}
