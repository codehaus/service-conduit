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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.sca4j.spi.services.contribution.ResourceElement;

/**
 * Represents a BPEL process definition.
 * 
 * @author meerajk
 *
 */
public class BpelProcessDefinition extends ResourceElement<QName> {
    
    private URL processUrl;
    private QName processName;
    private Map<QName, PartnerLink> partnerLinks = new HashMap<QName, PartnerLink>();
    private List<ReceiveActivity> receiveActivities = new ArrayList<ReceiveActivity>();
    private List<InvokeActivity> invokeActivities = new ArrayList<InvokeActivity>();
    
    /**
     * Initialises the process name and URL.
     * 
     * @param processUrl URL to the process definition.
     * @param processName Name of the process.
     */
    public BpelProcessDefinition(URL processUrl, QName processName) {
        super(processName);
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

    /**
     * @return Partner links available on the process.
     */
    public Map<QName, PartnerLink> getPartnerLinks() {
        return partnerLinks;
    }

    /**
     * @return Receive activities on the process.
     */
    public List<ReceiveActivity> getReceiveActivities() {
        return receiveActivities;
    }

    /**
     * @return Invoke activities on the process.
     */
    public List<InvokeActivity> getInvokeActivities() {
        return invokeActivities;
    }

}
