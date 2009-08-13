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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.sca4j.binding.ws.scdl;

import java.net.URI;
import java.util.Map;

import org.sca4j.scdl.BindingDefinition;
import org.sca4j.binding.ws.introspection.WsBindingLoader;
import org.w3c.dom.Document;

/**
 * Logical binding definition for web services.
 *
 * @version $Revision: 5465 $ $Date: 2008-09-21 23:12:21 +0100 (Sun, 21 Sep 2008) $
 */
public class WsBindingDefinition extends BindingDefinition {
    private static final long serialVersionUID = -2097314069798596206L;

    private final String implementation;
    private final String wsdlLocation;
    private final String wsdlElement;
    private Map<String, String> config;

    public WsBindingDefinition(URI targetUri, String implementation, String wsdlLocation, String wsdlElement, Document key) {
        super(targetUri, WsBindingLoader.BINDING_QNAME, key);
        this.implementation = implementation;
        this.wsdlElement = wsdlElement;
        this.wsdlLocation = wsdlLocation;        
    }

    public String getImplementation() {
        return implementation;
    }


    public String getWsdlElement() {
        return wsdlElement;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

}
