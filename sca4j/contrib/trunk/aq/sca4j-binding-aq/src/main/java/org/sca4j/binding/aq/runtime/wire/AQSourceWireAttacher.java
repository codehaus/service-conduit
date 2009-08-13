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
package org.sca4j.binding.aq.runtime.wire;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.XAQueueConnectionFactory;
import javax.sql.DataSource;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.aq.common.InitialState;
import org.sca4j.binding.aq.provision.AQWireSourceDefinition;
import org.sca4j.binding.aq.runtime.connectionfactory.ConnectionFactoryAccessor;
import org.sca4j.binding.aq.runtime.destination.DestinationFactory;
import org.sca4j.binding.aq.runtime.host.AQHost;
import org.sca4j.binding.aq.runtime.monitor.AQMonitor;
import org.sca4j.binding.aq.runtime.transport.OneWayMessageListener;
import org.sca4j.binding.aq.runtime.tx.TransactionHandler;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.resource.DataSourceRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Reference;

/**
 * AQ source wire attacher
 * 
 * @version $Revision: 3125 $ $Date: 2008-03-16 17:01:06 +0000 (Sun, 16 Mar 2008) $
 */
public class AQSourceWireAttacher implements SourceWireAttacher<AQWireSourceDefinition>, AQSourceWireAttacherMBean {

    private final Map<URI, WireSourceData> data;    
    private final Map<URI, AtomicBoolean> processState;    
    private ConnectionFactoryAccessor<XAQueueConnectionFactory> connectionFactoryAccessor;
    private DestinationFactory<XAQueueConnectionFactory> destinationFactory;
    private DataSourceRegistry  dataSourceRegistry;
    private TransactionHandler transactionHandler;
    private AQHost aqHost;
    private ClassLoaderRegistry classLoaderRegistry;
    private AQMonitor monitor;
    
    /**
     * Constructor
     */
    public AQSourceWireAttacher() {        
        data  = new ConcurrentHashMap<URI, WireSourceData>();        
        processState = new ConcurrentHashMap<URI, AtomicBoolean>();         
    }
    

    /**
     * Attaches the AQ binding
     */
    public void attachToSource(AQWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        URI uri = target.getUri();

        boolean started = source.getInitialState() == InitialState.STARTED ? true : false;
        processState.put(uri, new AtomicBoolean(started)); 
        setWireSourceData(source, target, wire);
        
        if (started) {
            startProcessing(uri);
        }
    }

    /**
     * Unregister the AQ host
     */
    public void detachFromSource(AQWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition wireTargetDefinition, Wire wire) throws WiringException {        
        URI service = wireTargetDefinition.getUri();       
        aqHost.unRegisterListener(service);
    }
    
    /**
     * Not Used
     */
    public void attachObjectFactory(AQWireSourceDefinition sourceDefinition, ObjectFactory<?> factory, PhysicalWireTargetDefinition targetDefinition) throws WiringException {
        throw new AssertionError();
    }  
    
    /**
     * Destroy
     */
    @Destroy
    public void destroy(){
        monitor.onSourceWire(" Cleaning ");
        data.clear();
        processState.clear();
    }

    /**
     * start from the jmx console
     */
    public void start(String uri) {
       URI service = URI.create(uri); 
       getProcessState(service).set(true);
       startProcessing(service);   
    }

    /**
     * stop from jmx console
     */
    public void stop(String serviceNamespace) {
        URI service = URI.create(serviceNamespace);
        getProcessState(service).set(false);
        aqHost.unRegisterListener(service);
    }

    /**
     * Gets the List of services
     */
    public List<String> getServiceNames() {
        List<String> services = new ArrayList<String>();
        for (URI service : data.keySet()) {
            services.add(service.toASCIIString());
        }
        return services;
    }  

    /**
     * Injects the Factory for retrieving Connection Factories
     * @param connectionFactoryAccessor
     */
    @Reference
    public void setConnectionFactoryAccessor(ConnectionFactoryAccessor<XAQueueConnectionFactory> connectionFactoryAccessor) {
        this.connectionFactoryAccessor = connectionFactoryAccessor;
    }

    /**
     * Injects the transaction handler.
     * @param transactionHandler Transaction handler.
     */
    @Reference(required = true)
    public void setTransactionHandler(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    /**
     * @param dataSource The dataSource to set.
     */
    @Reference(required = true)
    public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }

    /**
     * Injected JMS host.
     * 
     * @param jmsHost JMS Host to use.
     */
    @Reference(required = true)
    public void setJmsHost(AQHost aqHost) {
        this.aqHost = aqHost;
    }

    /**
     * Injects the destination strategies.
     * @param strategiesDestination strategies.
     */
    @Reference
    protected void setDestinationFactory(DestinationFactory<XAQueueConnectionFactory> destinationFactory) {
        this.destinationFactory = destinationFactory;
    }

    /**
     * Injects the class loader registry.
     * @param classLoaderRegistry
     */
    @Reference
    protected void setClassloaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    /**
     * Injects the monitor
     * @param monitor
     */
    @Monitor
    protected void setMonitor(AQMonitor monitor) {
        this.monitor = monitor;
    }

    /*
     * Gets the operational Methods for the service
     */
    private Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> getWireOpertaions(Wire wire) {
        Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> operations = new HashMap<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>>();

        /* Get the operation names */
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            operations.put(entry.getKey().getName(), entry);
        }
        return operations;
    }
    
    /*
     * Returns Consume State      
     */
    private AtomicBoolean getProcessState(URI serviceNamespace){
       AtomicBoolean consumeState = processState.get(serviceNamespace);
       return consumeState;
    }

    /*
     * Sets the wire source meta data
     */
    private void setWireSourceData(AQWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition, Wire wire) {
        WireSourceData wireData = new WireSourceData(sourceDefinition, targetDefinition, wire);
        data.put(targetDefinition.getUri(), wireData);
    }
    
    /*
     * Logic for getting the wire source data 
     */
    private WireSourceData getWireSourceData(URI serviceNamespace) {
        WireSourceData sourceData = data.get(serviceNamespace);
        if(sourceData == null){
            throw new IllegalArgumentException("The service name is not valid");
        }
        return sourceData;
    }

    /*
     * Gets the Destination 
     */
    private Destination getDestination(String destinationName, XAQueueConnectionFactory requestConnectionFactory) {
        Destination reqDestination = destinationFactory.getDestination(destinationName, requestConnectionFactory);
        return reqDestination;
    }

    /*
     * Gets the XAConnectionFactory 
     */
    private XAQueueConnectionFactory getFactory(String dataSourceKey) {
        DataSource dataSource = dataSourceRegistry.getDataSource(dataSourceKey);
        XAQueueConnectionFactory requestConnectionFactory = connectionFactoryAccessor.getConnectionFactory(dataSource);
        return requestConnectionFactory;
    }

    /**
     * Start the process to listen on the queues
     */
    private void startProcessing(URI serviceUri) {
        
        monitor.onSourceWire(" Attaching Source for " + serviceUri);        
        
        WireSourceData sourceData = getWireSourceData(serviceUri);
        
        AQWireSourceDefinition source = sourceData.getSourceDefinition();
        ClassLoader classloader = classLoaderRegistry.getClassLoader(source.getClassLoaderId());
        Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> ops = getWireOpertaions(sourceData.getWire());
        XAQueueConnectionFactory requestConnectionFactory = getFactory(source.getDataSourceKey());
        Destination reqDestination = getDestination(source.getDestinationName(), requestConnectionFactory);
        MessageListener listener = new OneWayMessageListener(ops);

        aqHost.registerListener(requestConnectionFactory, reqDestination, listener, transactionHandler, classloader, serviceUri, sourceData.getSourceDefinition().getConsumerCount());
        
    }   
}
