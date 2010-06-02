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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.Constants;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.scdl.JmsBindingDefinition;
import org.sca4j.host.Namespaces;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;

/**
 * @version $Revision: 5137 $ $Date: 2008-08-02 08:54:51 +0100 (Sat, 02 Aug
 *          2008) $
 */
@EagerInit
public class JmsBindingLoader implements TypeLoader<JmsBindingDefinition> {

    /**
     * Qualified name for the binding element.
     */
    public static final QName BINDING_QNAME = new QName(Constants.SCA_NS, "binding.jms");
    private static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();

    static {
        ATTRIBUTES.put("uri", "uri");
        ATTRIBUTES.put("correlationScheme", "correlationScheme");
        ATTRIBUTES.put("jndiURL", "jndiURL");
        ATTRIBUTES.put("initialContextFactory", "initialContextFactory");
        ATTRIBUTES.put("requires", "requires");
        ATTRIBUTES.put("policySets", "policySets");
        ATTRIBUTES.put("name", "name");
        ATTRIBUTES.put("create", "create");
        ATTRIBUTES.put("type", "type");
        ATTRIBUTES.put("destination", "destination");
        ATTRIBUTES.put("connectionFactory", "connectionFactory");
        ATTRIBUTES.put("JMSType", "JMSType");
        ATTRIBUTES.put("JMSTimeToLive", "JMSTimeToLive");
        ATTRIBUTES.put("JMSPriority", "JMSPriority");
        ATTRIBUTES.put("JMSDeliveryMode", "JMSDeliveryMode");
        ATTRIBUTES.put("JMSCorrelationId", "JMSCorrelationId");
        ATTRIBUTES.put("name", "name");
        ATTRIBUTES.put("pollingInterval", "pollingInterval");
        ATTRIBUTES.put("exceptionTimeout", "exceptionTimeout");
        ATTRIBUTES.put("consumerCount", "consumerCount");
    }

    private final LoaderHelper loaderHelper;

    /**
     * Constructor.
     * 
     * @param loaderHelper the loaderHelper
     * @param transformationService the JAXB transformation service
     */
    public JmsBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    public JmsBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        validateAttributes(reader, introspectionContext);

        JmsBindingMetadata metadata = new JmsBindingMetadata();
        JmsBindingDefinition bd = new JmsBindingDefinition(metadata, loaderHelper.loadKey(reader));
        
        final String correlationScheme = reader.getAttributeValue(null, "correlationScheme");
        if (correlationScheme != null) {
            metadata.correlationScheme = CorrelationScheme.valueOf(correlationScheme);
        }
        
        metadata.jndiUrl = reader.getAttributeValue(null, "jndiURL");
        metadata.initialContextFactory = reader.getAttributeValue(null, "initialContextFactory");
        loaderHelper.loadPolicySetsAndIntents(bd, reader, introspectionContext);
        
        String pollingInterval = reader.getAttributeValue(Namespaces.SCA4J_NS, "pollingInterval");
        if (pollingInterval != null) {
            metadata.pollingInterval = Integer.parseInt(pollingInterval);
        }
        String exceptionTimeout = reader.getAttributeValue(Namespaces.SCA4J_NS, "exceptionTimeout");
        if (exceptionTimeout != null) {
            metadata.exceptionTimeout = Integer.parseInt(exceptionTimeout);
        }
        String consumerCount = reader.getAttributeValue(Namespaces.SCA4J_NS, "consumerCount");
        if (consumerCount != null) {
            metadata.consumerCount = Integer.parseInt(consumerCount);
        }
        
        String name;
        while (true) {

            switch (reader.next()) {
            case START_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("destination".equals(name)) {
                    metadata.destinationName = reader.getAttributeValue(null, "name");
                } else if ("connectionFactory".equals(name)) {
                    metadata.connectionFactoryName = reader.getAttributeValue(null, "name");
                } else if ("response".equals(name)) {
                   loadResponse(reader, metadata);
                }
                break;
            case END_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("binding.jms".equals(name)) {
                    return bd;
                }
                break;
            }

        }

    }

    /*
     * Loads response definition.
     */
    private void loadResponse(XMLStreamReader reader, JmsBindingMetadata metadata) throws XMLStreamException {

        String name;
        while (true) {

            switch (reader.next()) {
            case START_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("destination".equals(name)) {
                    metadata.responseDestinationName = reader.getAttributeValue(null, "name");
                } else if ("connectionFactory".equals(name)) {
                    metadata.responseConnectionFactoryName = reader.getAttributeValue(null, "name");
                }
                break;
            case END_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("response".equals(name)) {
                    return;
                }
                break;
            }

        }

    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!ATTRIBUTES.containsKey(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
