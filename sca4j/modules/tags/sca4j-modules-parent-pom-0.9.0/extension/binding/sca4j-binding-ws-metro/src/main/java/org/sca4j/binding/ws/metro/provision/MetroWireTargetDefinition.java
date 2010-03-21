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
package org.sca4j.binding.ws.metro.provision;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.sca4j.binding.ws.provision.WsdlElement;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * @version $Revision$ $Date$
 */
public class MetroWireTargetDefinition extends PhysicalWireTargetDefinition {

    private String referenceInterface;
    private Map<String, Map<String, String>> operationInfo;
    private Map<String, String> config;
    private URI classloaderURI;
    private String wsdlLocation;
    private WsdlElement wsdlElement;

    /**
     * @return Reference interface for the wire target.
     */
    public String getReferenceInterface() {
        return referenceInterface;
    }

    /**
     * @param referenceInterface
     *            Reference interface for the wire target.
     */
    public void setReferenceInterface(String referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    /**
     * @return Classloader URI.
     */
    public URI getClassloaderURI() {
        return classloaderURI;
    }

    /**
     * @param classloaderURI
     *            Classloader URI.
     */
    public void setClassloaderURI(URI classloaderURI) {
        this.classloaderURI = classloaderURI;
    }


    public Map<String, Map<String, String>> getOperationInfo() {
        return operationInfo;
    }

    public void addOperationInfo(String operation, Map<String, String> operationInfo) {
        if (this.operationInfo == null) {
            this.operationInfo = new HashMap<String, Map<String, String>>();
        }
        this.operationInfo.put(operation, operationInfo);
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    
    
    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public WsdlElement getWsdlElement() {
        return wsdlElement;
    }

    public void setWsdlElement(WsdlElement wsdlElement) {
        this.wsdlElement = wsdlElement;
    }

}
