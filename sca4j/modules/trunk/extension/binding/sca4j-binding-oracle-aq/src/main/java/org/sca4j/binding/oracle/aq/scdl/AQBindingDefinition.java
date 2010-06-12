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
package org.sca4j.binding.oracle.aq.scdl;

import javax.xml.namespace.QName;

import oracle.AQ.AQOracleDriver;

import org.oasisopen.sca.Constants;
import org.sca4j.binding.oracle.aq.common.InitialState;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * AQBinding Definition.
 */
public class AQBindingDefinition extends BindingDefinition {
    
	private static final QName BINDING_QNAME  =  new QName(Constants.SCA_NS, "binding.oracle.aq");
	
	static {
	    try {
	        Class.forName(AQOracleDriver.class.getName());
	    } catch (ClassNotFoundException e) {
	        throw new ExceptionInInitializerError(e);
	    }
	}

    public String destinationName;
    public String responseDestinationName;
    public String dataSourceKey;
    public String correlationId;
    
    public InitialState initialState = InitialState.STARTED;
    public int consumerCount = 1;
    public long exceptionTimeout = 60 * 1000L;
    public int delay = 0;
    public long consumerDelay = 5 * 1000L;
    
    /**
     * @param documentKey If the binding is used with a key.
     */
    public AQBindingDefinition(Document documentKey) {
        super(null, BINDING_QNAME, documentKey);
    }   
    
}
