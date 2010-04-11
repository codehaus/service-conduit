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
package org.sca4j.binding.oracle.aq.introspection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.oracle.aq.common.InitialState;
import org.sca4j.binding.oracle.aq.scdl.AQBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;
import org.w3c.dom.Document;



/**
 * Introspect's the XML.
 */

@EagerInit
public class AQBindingLoader implements TypeLoader<AQBindingDefinition> {

    
    private static final int DEFAULT_DELAY = 10;
    private static final int DEFAULT_CONSUMERS = 1;
    private static final long DEFAULT_CONSUMER_DELAY = 0L;

    /** The loader helper. */
    private final LoaderHelper loaderHelper;

    /**
     * Constructor.
     *
     * @param loaderHelper the loader helper
     */
    public AQBindingLoader(final @Reference LoaderHelper loaderHelper) {
          this.loaderHelper = loaderHelper;
    }

    /**
     * Introspect the XML.
     *
     * @param reader the reader
     * @param loaderContext the loader context
     *
     * @return the AQ binding definition
     *
     * @throws XMLStreamException the XML stream exception
     * @throws LoaderException the loader exception
     */
    public AQBindingDefinition load(final XMLStreamReader reader, final IntrospectionContext loaderContext) throws XMLStreamException {
        
    	final Document documentKey = loaderHelper.loadKey(reader);
        
        String destinationName = reader.getAttributeValue(null, "destinationName");
        String sInitialState = reader.getAttributeValue(null, "initialState");
        String sConsumerCount = reader.getAttributeValue(null, "consumerCount");
        String sConsumerDelay = reader.getAttributeValue(null, "consumerDelay");
        String dataSourceKey = reader.getAttributeValue(null, "dataSourceKey");
        String correlationId = reader.getAttributeValue(null, "correlationId");
        String sDelay = reader.getAttributeValue(null, "delay");

        int consumerCount = sConsumerCount != null ? Integer.parseInt(sConsumerCount) : DEFAULT_CONSUMERS;
        long consumerDelay = sConsumerDelay != null ? Long.parseLong(sConsumerDelay) : DEFAULT_CONSUMER_DELAY;
        int delay = sDelay != null ? Integer.parseInt(sDelay) : DEFAULT_DELAY;
        InitialState initialState = sInitialState != null ? InitialState.valueOf(sInitialState) : InitialState.STARTED;
        
        if(dataSourceKey == null || dataSourceKey.equals("")){
            throw new IllegalArgumentException(" DataSource Key For AQ Binding is Not Configured ");
        }

        AQBindingDefinition bindingDefinition = new AQBindingDefinition(destinationName, initialState, 
            		                                                    dataSourceKey, consumerCount, consumerDelay, 
            		                                                    delay, correlationId, documentKey);

        loaderHelper.loadPolicySetsAndIntents(bindingDefinition, reader, loaderContext);

        LoaderUtil.skipToEndElement(reader);

        return bindingDefinition;
    }
}
