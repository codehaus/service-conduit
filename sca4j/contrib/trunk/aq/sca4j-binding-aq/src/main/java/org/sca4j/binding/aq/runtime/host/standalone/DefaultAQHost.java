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
package org.sca4j.binding.aq.runtime.host.standalone;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.XAConnection;
import javax.jms.XAQueueConnectionFactory;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.aq.common.SCA4JAQException;
import org.sca4j.binding.aq.runtime.helper.JmsHelper;
import org.sca4j.binding.aq.runtime.host.AQHost;
import org.sca4j.binding.aq.runtime.host.PollingConsumer;
import org.sca4j.binding.aq.runtime.monitor.AQMonitor;
import org.sca4j.binding.aq.runtime.tx.TransactionHandler;
import org.sca4j.spi.services.work.WorkScheduler;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Handler for AQ
 * 
 * @version $Revsion$ $Date: 2008-06-11 20:01:35 +0100 (Wed, 11 Jun 2008) $
 */
public class DefaultAQHost implements AQHost, DefaultAQHostMBean {

    private final Map<URI, XAConnection> connections;
    private final Map<URI, List<PollingConsumer>> consumers;
    private final Map<URI, WorkData> workData;
    private WorkScheduler workScheduler;
    private AQMonitor monitor;
    private int receiverCount = 1;
    private long readTimeout = 5000L;

    /**
     * Constructor
     */
    public DefaultAQHost() {
        connections = new ConcurrentHashMap<URI, XAConnection>();
        consumers = new ConcurrentHashMap<URI, List<PollingConsumer>>();
        workData = new ConcurrentHashMap<URI, WorkData>();
    }

    /**
     * Registers the listeners to start on consuming messages
     */
    public void registerListener(XAQueueConnectionFactory connectionFactory, Destination destination, MessageListener listener, TransactionHandler transactionHandler, 
                                                                                   ClassLoader classLoader, URI namespace, int receiverCount) {
        
        final WorkData data = new WorkData();
        /* Set the target URI */
        data.setConnectionFactory(connectionFactory);
        data.setDestination(destination);
        data.setListener(listener);
        data.setClassLoader(classLoader);
        data.setTxHandler(transactionHandler);
        workData.put(namespace, data);
        this.receiverCount = receiverCount;
        try {
            prepareWorkSchedule(namespace);
        } catch (JMSException ex) {
            throw new SCA4JAQException("Unable to Start serviceing Requests", ex);
        }
    }
    
    /**
     * Unregisters the Listeners
     */
    public void unRegisterListener(final URI serviceName) {
        monitor.onAQHost("Unregistering AQ host for " + serviceName);
        workData.remove(serviceName);
        stopProcessingOnService(serviceName);
    }

    /**
     * Stops the receiver threads.
     * @throws JMSException
     */
    @Destroy
    public void stop() throws JMSException {
        workData.clear();
        stopConsumers();
        closeConnections();
        monitor.stopOnAQHost(" Stopped ");
    }

    /**
     * Sets The Number Of receivers
     * @return count
     */
    public void setReceivers(final String namespace, final int receivers) {
        final URI serviceName = URI.create(namespace);
        if(!workData.containsKey(serviceName)){
            throw new IllegalStateException("Please start the AQ Source Wire Attacher");
        }       
        try {
            stopProcessingOnService(serviceName);
            receiverCount = receivers;
            prepareWorkSchedule(serviceName);
        } catch (JMSException je) {
            monitor.onException(je);
            throw new SCA4JAQException("Unable to Start servicing Requests from Managment Console", je);
        }
    }

    /**
     * Gets the RecieverCount
     * @return count
     */
    public int getReceiverCount() {
        return receiverCount;
    }

    /**
     * Returns the Destinations
     * @return Llist of names 
     */
    public List<String> getDestination()  {
        final List<String> destinations = new LinkedList<String>();
        for(WorkData data : workData.values()){
            destinations.add(destinationName(data.getDestination()));
        }
        return destinations;
    }    

    /**
     * Injects the work scheduler.
     * 
     * @param workScheduler
     *            Work scheduler to be used.
     */
    @Reference
    protected void setWorkScheduler(final WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    /**
     * Sets the read timeout.
     * 
     * @param readTimeout
     *            Read timeout for blocking receive.
     */
    @Property
    protected void setReadTimeout(final long readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Sets the Monitor
     * 
     * @param monitor
     */
    @Monitor
    protected void setMonitor(AQMonitor monitor) {
        this.monitor = monitor;
    }

    /*
     * Prepares the work schedule
     */
    private void prepareWorkSchedule(final URI serviceNamespace) throws JMSException {
        final List<PollingConsumer> consumerList = new LinkedList<PollingConsumer>();
        final WorkData data = workData.get(serviceNamespace);
        final XAConnection connection = data.getConnectionFactory().createXAConnection();

        for (int i = 0; i < receiverCount; i++) {
            startConsumption(serviceNamespace, connection, data, consumerList);
        }

        connection.start();
        consumers.put(serviceNamespace, consumerList);
        connections.put(serviceNamespace, connection);
        monitor.onAQHost(" Started ");
    }

    /*
     * Start the Consumers to process Messages
     */
    private void startConsumption(final URI serviceName, final XAConnection connection, final WorkData data, final List<PollingConsumer> list) throws JMSException {
        final PollingConsumer pollingConsumer = new ConsumerWorker(connection, data.getDestination(), data.getListener(), data.getTxHandler(), 
                                                                   readTimeout, data.getClassLoader(), monitor);
        workScheduler.scheduleWork(pollingConsumer);
        list.add(pollingConsumer);
    }
        
    /*
     * Stops Consumption Of messages
     */
    private void stopConsumers() {
        for (List<PollingConsumer> consumerList : consumers.values()) {
            stopConsumers(consumerList);
        }
        consumers.clear();
    }

    /*
     * Closes the Connections
     */
    private void closeConnections() {
        for (XAConnection connection : connections.values()) {
            closeConnections(connection);
        }
        connections.clear();
    }
    
    /*
     * Stop all Processing for a given service
     */
    private void stopProcessingOnService(final URI serviceName){
        final List<PollingConsumer> consumerList = consumers.get(serviceName);
        final XAConnection connection = connections.get(serviceName);
        stopConsumers(consumerList);
        closeConnections(connection);
        consumers.remove(serviceName);
        connections.remove(serviceName);
    }

    /*
     * Stop Individual Consumer
     */
    private void stopConsumers(final List<PollingConsumer> consumerList) {
        int i = 0;
        for (PollingConsumer consumer : consumerList) {
            monitor.stopConsumer(++i + " Stopping ");
            consumer.stopConsumption();
        }
        consumerList.clear();
    }

    /*
     * Stops the Individual Connection
     */
    private void closeConnections(final XAConnection connection) {
        monitor.stopConnection(" Stopping ");
        JmsHelper.closeQuietly(connection);
    }
    
    /*
     * returns the name of the destination
     */
    private String destinationName(final Destination destination) {
       try {
           return ((Queue)destination).getQueueName();
       }catch(JMSException je) {
           throw new IllegalStateException("Error when getting Name");
       }
    }
}
