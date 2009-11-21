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
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
        destination.setCreate(CreateOption.never); // always assume the
                                                   // destination already exists
        result.setDestination(destination);

        // ConnectionFactory
        ConnectionFactoryDefinition connectionFactory = new ConnectionFactoryDefinition();
        String connectionFactoryName = uriProperties.get(JmsURIMetadata.CONNECTIONFACORYNAME);
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
     * Generate an URI from JmsBindingMetadata. This may be removed when call
     * eliminate dependency on binding's URI.
     */
    static String generateURI(JmsBindingMetadata metadata) {
        StringBuilder builder = new StringBuilder();
        builder.append("jms:").append(metadata.getDestination().getName()).append("?connectionFactory=").append(metadata.getConnectionFactory().getName());
        return builder.toString();
    }
}
