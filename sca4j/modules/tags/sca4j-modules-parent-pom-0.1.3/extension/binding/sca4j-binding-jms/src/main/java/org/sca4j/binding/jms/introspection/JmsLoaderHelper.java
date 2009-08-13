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
package org.sca4j.binding.jms.introspection;

import java.util.Map;

import org.sca4j.binding.jms.common.ConnectionFactoryDefinition;
import org.sca4j.binding.jms.common.CreateOption;
import org.sca4j.binding.jms.common.DestinationDefinition;
import org.sca4j.binding.jms.common.DestinationType;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.JmsURIMetadata;
import org.sca4j.binding.jms.common.ResponseDefinition;

/**
 * Helper class for JMS loader.
 */
public class JmsLoaderHelper {
    private static final String DEFAULT_CLIENT_QUEUE = "clientQueue";
    /**
     * Jndi name for default ConnectionFactory
     */
    private static final String DEFAULT_JMS_CONNECTION_FACTORY = "connectionFactory";

    private JmsLoaderHelper() {
    }

    /**
     * Transform a JmsURIMetadata object to a JmsBindingMetadata.
     *
     * @param uriMeta JmsURIMetadata
     * @return a equivalent JmsURIMetadata object
     */
    static JmsBindingMetadata getJmsMetadataFromURI(JmsURIMetadata uriMeta) {
        JmsBindingMetadata result = new JmsBindingMetadata();
        Map<String, String> uriProperties = uriMeta.getProperties();

        // Destination
        DestinationDefinition destination = new DestinationDefinition();
        String destinationType = uriProperties.get(JmsURIMetadata.DESTINATIONTYPE);
        if ("topic".equalsIgnoreCase(destinationType)) {
            destination.setDestinationType(DestinationType.topic);
        }
        destination.setName(uriMeta.getDestination());
        destination.setCreate(CreateOption.never); // always assume the destination already exists
        result.setDestination(destination);

        // ConnectionFactory
        ConnectionFactoryDefinition connectionFactory = new ConnectionFactoryDefinition();
        String connectionFactoryName = uriProperties
                .get(JmsURIMetadata.CONNECTIONFACORYNAME);
        if (connectionFactoryName == null) {
            connectionFactory.setName(DEFAULT_JMS_CONNECTION_FACTORY);
        } else {
            connectionFactory.setName(connectionFactoryName);
        }
        connectionFactory.setCreate(CreateOption.never);
        result.setConnectionFactory(connectionFactory);

        // Response copy configuration of request
        ResponseDefinition response = new ResponseDefinition();
        response.setConnectionFactory(connectionFactory);
        DestinationDefinition responseDestinationDef = new DestinationDefinition();
        String responseDestination = uriProperties.get(JmsURIMetadata.RESPONSEDESTINAT);
        if (responseDestination != null) {
            responseDestinationDef.setName(responseDestination);
        } else {
            responseDestinationDef.setName(DEFAULT_CLIENT_QUEUE);

        }
        responseDestinationDef.setCreate(CreateOption.never);
        response.setDestination(responseDestinationDef);
        result.setResponse(response);
        return result;
    }

    /**
     * Generate an URI from JmsBindingMetadata. This may be removed when call eliminate dependency on binding's URI.
     */
    static String generateURI(JmsBindingMetadata metadata) {
        StringBuilder builder = new StringBuilder();
        builder.append("jms:").append(metadata.getDestination().getName())
                .append("?connectionFactory=").append(
                metadata.getConnectionFactory().getName());
        return builder.toString();
    }
}
