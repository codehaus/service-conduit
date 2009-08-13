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
package org.sca4j.binding.jms.runtime;

import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.osoa.sca.annotations.Reference;

import org.sca4j.binding.jms.common.ConnectionFactoryDefinition;
import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.CreateOption;
import org.sca4j.binding.jms.common.DestinationDefinition;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.provision.JmsWireSourceDefinition;
import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.binding.jms.runtime.host.JmsHost;
import org.sca4j.binding.jms.runtime.lookup.connectionfactory.ConnectionFactoryStrategy;
import org.sca4j.binding.jms.runtime.lookup.destination.DestinationStrategy;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches the target end of a wire (a service) to a JMS queue.
 *
 * @version $Revision: 5363 $ $Date: 2008-09-09 01:39:36 +0100 (Tue, 09 Sep 2008) $
 */
public class JmsSourceWireAttacher implements SourceWireAttacher<JmsWireSourceDefinition>, JmsSourceWireAttacherMBean {

    private JmsHost jmsHost;
    private Map<CreateOption, DestinationStrategy> destinationStrategies = new HashMap<CreateOption, DestinationStrategy>();
    private Map<CreateOption, ConnectionFactoryStrategy> connectionFactoryStrategies = new HashMap<CreateOption, ConnectionFactoryStrategy>();
    private ClassLoaderRegistry classLoaderRegistry;
    private TransactionHandler transactionHandler;

    /**
     * Injects the transaction handler.
     *
     * @param transactionHandler Transaction handler.
     */
    @Reference
    public void setTransactionHandler(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    /**
     * Injects the destination strategies.
     *
     * @param strategies Destination strategies.
     */
    @Reference
    public void setDestinationStrategies(Map<CreateOption, DestinationStrategy> strategies) {
        this.destinationStrategies = strategies;
    }

    /**
     * Injects the connection factory strategies.
     *
     * @param strategies Connection factory strategies.
     */
    @Reference
    public void setConnectionFactoryStrategies(Map<CreateOption, ConnectionFactoryStrategy> strategies) {
        this.connectionFactoryStrategies = strategies;
    }

    /**
     * Injects the classloader registry.
     *
     * @param classLoaderRegistry Classloader registry.
     */
    @Reference
    public void setClassloaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    /**
     * Injected JMS host.
     *
     * @param jmsHost JMS Host to use.
     */
    @Reference
    public void setJmsHost(JmsHost jmsHost) {
        this.jmsHost = jmsHost;
    }

    public void attachToSource(JmsWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {

        JMSObjectFactory responseJMSObjectFactory = null; 
        URI serviceUri = target.getUri();

        ClassLoader cl = classLoaderRegistry.getClassLoader(source.getClassLoaderId());

        JmsBindingMetadata metadata = source.getMetadata();
        Hashtable<String, String> env = metadata.getEnv();
        CorrelationScheme correlationScheme = metadata.getCorrelationScheme();
        TransactionType transactionType = source.getTransactionType();

        ConnectionFactoryDefinition connectionFactory = metadata.getConnectionFactory();
        DestinationDefinition destination = metadata.getDestination();
        JMSObjectFactory requestJMSObjectFactory = buildObjectFactory(connectionFactory, destination, env);
        
        if(!metadata.noResponse()){
          ConnectionFactoryDefinition responseConnectionFactory = metadata.getResponseConnectionFactory();
          DestinationDefinition responseDestination = metadata.getResponseDestination();
          responseJMSObjectFactory = buildObjectFactory(responseConnectionFactory, responseDestination, env);
        }

        String callbackUri = null;
        if (target.getCallbackUri() != null) {
            callbackUri = target.getCallbackUri().toString();
        }

        Map<String, PayloadType> messageTypes = source.getPayloadTypes();
        Map<PhysicalOperationDefinition, InvocationChain> operations = wire.getInvocationChains();

        ResponseMessageListener messageListener;
         if(metadata.noResponse()){
             messageListener = new OneWayMessageListenerImpl(operations, messageTypes);
         }else {
              messageListener =  new ResponseMessageListenerImpl(operations, correlationScheme, messageTypes, transactionType, callbackUri);
         }
        jmsHost.registerResponseListener(requestJMSObjectFactory,
                                         responseJMSObjectFactory,
                                         messageListener,
                                         transactionType,
                                         transactionHandler,
                                         cl,
                                         serviceUri);
    }

    public void detachFromSource(JmsWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        jmsHost.unregisterListener(target.getUri());
    }

    public void attachObjectFactory(JmsWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition)
            throws WiringException {
        throw new UnsupportedOperationException();
    }

    private JMSObjectFactory buildObjectFactory(ConnectionFactoryDefinition connectionFactoryDefinition,
                                                DestinationDefinition destinationDefinition,
                                                Hashtable<String, String> env) {
        CreateOption create = connectionFactoryDefinition.getCreate();

        ConnectionFactory connectionFactory =
                connectionFactoryStrategies.get(create).getConnectionFactory(connectionFactoryDefinition, env);
        create = destinationDefinition.getCreate();
        Destination reqDestination =
                destinationStrategies.get(create).getDestination(destinationDefinition, connectionFactory, env);
        return new JMSObjectFactory(connectionFactory, reqDestination, 1);
    }


}
