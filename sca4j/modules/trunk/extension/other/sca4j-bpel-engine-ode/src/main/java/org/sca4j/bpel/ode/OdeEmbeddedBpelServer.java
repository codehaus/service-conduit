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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Executors;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactory;

import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactory;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.scheduler.simple.DatabaseDelegate;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.utils.GUID;
import org.apache.ode.utils.xsl.XslTransformHandler;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.resource.ResourceRegistry;
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
    @Reference public ResourceRegistry resourceRegistry;
    
    private BpelServerImpl bpelServer;
    
    @Destroy
    public void stop() {
        //bpelServer.shutdown();
    }

    @Override
    public synchronized void addOutboundEndpoint(QName processName, QName referenceName, Interceptor invoker) {
    }

    @Override
    public Message invokeService(PhysicalOperationDefinition targetOperationDefinition, QName processName, Message message) {
        //MyRoleMessageExchange mex = bpelServer.getEngine().createMessageExchange(new GUID().toString(), processName, targetOperationDefinition.getName());
        // TODO Auto-generated method stub
        return new MessageImpl();
    }

    @Override
    public synchronized void registerProcess(BpelPhysicalComponentDefinition physicalComponentDefinition) {
        if (bpelServer == null) {
            start();
        }
        try {
            Sca4jProcessConf sca4jProcessConf = new Sca4jProcessConf(physicalComponentDefinition);
            bpelServer.register(sca4jProcessConf);
        } catch (CompilationException e) {
            throw new Sca4jOdeException("Unable to compile bpel process " + physicalComponentDefinition.getProcessUrl(), e);
        } catch (URISyntaxException e) {
            throw new Sca4jOdeException("Unable to register bpel process " + physicalComponentDefinition.getProcessUrl(), e);
        } catch (IOException e) {
            throw new Sca4jOdeException("Unable to register bpel process " + physicalComponentDefinition.getProcessUrl(), e);
        }
    }
    
    private void start() {
        
        XslTransformHandler.getInstance().setTransformerFactory(TransformerFactory.newInstance());

        SimpleScheduler scheduler = getScheduler();
        BpelDAOConnectionFactory daoConnectionFactory = new BpelDAOConnectionFactoryImpl(scheduler);
        
        bpelServer = new BpelServerImpl();
        bpelServer.setDaoConnectionFactory(daoConnectionFactory);
        bpelServer.setScheduler(scheduler);
        
        bpelServer.setBindingContext(new Sca4jBindingContext());
        
        bpelServer.init();
        
        System.err.print("BPEL server initialized");
        
    }

    /*
     * Gets the scheduler.
     */
    private SimpleScheduler getScheduler() {
        DataSource dataSource = resourceRegistry.getResource(DataSource.class, "OdeDS");
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
