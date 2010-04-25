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
package org.sca4j.bpel.ode;

import java.util.Properties;
import java.util.concurrent.Executors;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.apache.ode.bpel.dao.BpelDAOConnectionFactory;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.scheduler.simple.DatabaseDelegate;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.utils.GUID;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.spi.resource.DataSourceRegistry;
import org.sca4j.spi.wire.Interceptor;

/**
 * Implementation of the embedded BPEL server SPI using ODE.
 * 
 * @author meerajk
 *
 */
@EagerInit
public class OdeEmbeddedBpelServer implements EmbeddedBpelServer {
    
    @Reference public TransactionManager transactionManager;
    @Reference public DataSourceRegistry dataSourceRegistry;
    
    private BpelServerImpl bpelServer;
    
    @Destroy
    public void stop() {
        bpelServer.shutdown();
    }

    @Override
    public synchronized void addOutboundEndpoint(QName processName, QName referenceName, Interceptor invoker) {
    }

    @Override
    public synchronized Object invokeService(QName procesName, QName serviceName, String operation, Object payload) {
        return null;
    }

    @Override
    public synchronized void registerProcess(BpelPhysicalComponentDefinition physicalComponentDefinition) {
        if (bpelServer == null) {
            start();
        }
    }
    
    private void start() {

        SimpleScheduler scheduler = getScheduler();
        BpelDAOConnectionFactory daoConnectionFactory = new BpelDAOConnectionFactoryImpl(scheduler);
        
        bpelServer = new BpelServerImpl();
        bpelServer.setDaoConnectionFactory(daoConnectionFactory);
        bpelServer.setScheduler(scheduler);
        
        bpelServer.init();
        
        System.err.print("BPEL server initialized");
        
    }

    /*
     * Gets the scheduler.
     */
    private SimpleScheduler getScheduler() {
        DataSource dataSource = dataSourceRegistry.getDataSource("OdeDS");
        if (dataSource == null) {
            throw new IllegalStateException("Datasource not found: OdeDS");
        }
        
        DatabaseDelegate databaseDelegate = new JdbcDelegate(dataSource);
        String nodeId = new GUID().toString();
        Properties schedulerProperties = new Properties();
        
        SimpleScheduler scheduler = new SimpleScheduler(nodeId, databaseDelegate, schedulerProperties);
        scheduler.setExecutorService(Executors.newCachedThreadPool());
        scheduler.setTransactionManager(transactionManager);
        return scheduler;
    }

}
