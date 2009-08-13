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

import java.util.Map;

import javax.jms.Destination;
import javax.jms.XAQueueConnectionFactory;
import javax.sql.DataSource;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.aq.provision.AQWireTargetDefinition;
import org.sca4j.binding.aq.runtime.connectionfactory.ConnectionFactoryAccessor;
import org.sca4j.binding.aq.runtime.destination.DestinationFactory;
import org.sca4j.binding.aq.runtime.monitor.AQMonitor;
import org.sca4j.binding.aq.runtime.tx.TransactionHandler;
import org.sca4j.binding.aq.runtime.wire.interceptor.OneWayAQTargetInterceptor;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.resource.DataSourceRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;
import org.osoa.sca.annotations.Reference;

/**
 * Wire attacher for AQ binding.
 * @version $Revision: 4817 $ $Date: 2008-06-11 20:01:35 +0100 (Wed, 11 Jun 2008) $
 */
public class AQTargetWireAttacher implements  TargetWireAttacher<AQWireTargetDefinition> {   

    private ConnectionFactoryAccessor<XAQueueConnectionFactory> connectionFactoryAccessor;
    private DestinationFactory<XAQueueConnectionFactory> destinationFactory;
    private ClassLoaderRegistry classLoaderRegistry;
    private DataSourceRegistry dataSourceRegistry;
    private TransactionHandler transactionHandler;
    private AQMonitor monitor;    
    

    public void attachToTarget(PhysicalWireSourceDefinition source, AQWireTargetDefinition target, Wire wire)  throws WiringException {
        
        monitor.onTargetWire(" Still Refactoring ");
        
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(target.getClassLoaderId());  

        String dataSourceKey = target.getDataSourceKey();
        DataSource dataSource = dataSourceRegistry.getDataSource(dataSourceKey);

        XAQueueConnectionFactory reqCf = connectionFactoryAccessor.getConnectionFactory(dataSource);

        String destinationName = target.getDestinationName();
        Destination reqDestination = destinationFactory.getDestination(destinationName, reqCf);     
         
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationDefinition op = entry.getKey();
            InvocationChain chain = entry.getValue();            
            Interceptor interceptor = new OneWayAQTargetInterceptor(op.getName(), reqCf, reqDestination, transactionHandler, classLoader);             
            chain.addInterceptor(interceptor);
        }
        
    }

    public ObjectFactory<?> createObjectFactory(AQWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }       
    
    /**
     * Injects the Factory for retrieving Connection Factories
     * @param connectionFactoryAccessor
     */
    @Reference
    public void setConnectionFactoryAccessor(ConnectionFactoryAccessor<XAQueueConnectionFactory> connectionFactoryAccessor){
        this.connectionFactoryAccessor = connectionFactoryAccessor;
    }
            
    /**
     * Injects the destination {@link DestinationFactory}
     * @param  destinationFactory
     */
    @Reference
    public void setDestinationFactory(DestinationFactory<XAQueueConnectionFactory> destinationFactory) {
        this.destinationFactory = destinationFactory;
    }
    
    /**
     * @param dataSource - the dataSource to set.
     */
    @Reference
    public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
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
     * Injects the class loader registry.
     * @param classLoaderRegistry
     */
    @Reference
    public void setClassloaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }
    
    /**
     * Injects the monitor
     * @param monitor
     */
    @Monitor
    public void setMonitor(AQMonitor monitor){
        this.monitor = monitor;
    }
}
