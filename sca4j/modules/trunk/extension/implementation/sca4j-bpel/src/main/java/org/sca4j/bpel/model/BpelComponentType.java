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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.scdl.ServiceDefinition;

public class BpelComponentType extends AbstractComponentType<ServiceDefinition, ReferenceDefinition, Property, ResourceDefinition> {
    
    private URL processUrl;
    private QName processName;
    private Map<QName, Definition> portTypes = new HashMap<QName, Definition>();
    private Map<QName, String> portTypeToPartnerLinks = new HashMap<QName, String>();
    
    /**
     * Initialises the process name and URL.
     * 
     * @param processUrl URL to the process definition.
     * @param processName Name of the process.
     */
    public BpelComponentType(URL processUrl, QName processName) {
        this.processUrl = processUrl;
        this.processName = processName;
    }

    /**
     * @return URL to the process definition.
     */
    public URL getProcessUrl() {
        return processUrl;
    }

    /**
     * @return Name of the process.
     */
    public QName getProcessName() {
        return processName;
    }

    public Map<QName, Definition> getPortTypes() {
        return portTypes;
    }

    public Map<QName, String> getPortTypeToPartnerLinks() {
        return portTypeToPartnerLinks;
    }

}
