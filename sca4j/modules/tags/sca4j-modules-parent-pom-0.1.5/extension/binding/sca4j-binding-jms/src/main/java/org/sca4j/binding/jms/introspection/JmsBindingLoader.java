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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.binding.jms.common.ConnectionFactoryDefinition;
import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.CreateOption;
import org.sca4j.binding.jms.common.DestinationDefinition;
import org.sca4j.binding.jms.common.DestinationType;
import org.sca4j.binding.jms.common.HeadersDefinition;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.JmsURIMetadata;
import org.sca4j.binding.jms.common.OperationPropertiesDefinition;
import org.sca4j.binding.jms.common.PropertyAwareObject;
import org.sca4j.binding.jms.common.ResponseDefinition;
import org.sca4j.binding.jms.scdl.JmsBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.jaxb.control.api.JAXBTransformationService;

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
    }

    private final LoaderHelper loaderHelper;

    /**
     * Constructor.
     * 
     * @param loaderHelper the loaderHelper
     * @param transformationService the JAXB transformation service
     */
    public JmsBindingLoader(@Reference LoaderHelper loaderHelper, @Reference JAXBTransformationService transformationService) {
        this.loaderHelper = loaderHelper;
        transformationService.registerBinding(BINDING_QNAME, JAXBTransformationService.DATATYPE_XML);
    }

    public JmsBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        validateAttributes(reader, introspectionContext);

        JmsBindingMetadata metadata;
        String uri = reader.getAttributeValue(null, "uri");
        JmsBindingDefinition bd;
        if (uri != null) {
            JmsURIMetadata uriMeta;
            try {
                uriMeta = JmsURIMetadata.parseURI(uri);
                metadata = JmsLoaderHelper.getJmsMetadataFromURI(uriMeta);
            } catch (URISyntaxException e) {
                InvalidValue failure = new InvalidValue("Invalid JMS binding URI: " + uri, uri, reader, e);
                introspectionContext.addError(failure);
                return null;
            }
            bd = new JmsBindingDefinition(loaderHelper.getURI(uri), metadata, loaderHelper.loadKey(reader));
        } else {
            metadata = new JmsBindingMetadata();
            bd = new JmsBindingDefinition(metadata, loaderHelper.loadKey(reader));
        }
        final String correlationScheme = reader.getAttributeValue(null, "correlationScheme");
        if (correlationScheme != null) {
            metadata.setCorrelationScheme(CorrelationScheme.valueOf(correlationScheme));
        }
        metadata.setJndiUrl(reader.getAttributeValue(null, "jndiURL"));
        metadata.setInitialContextFactory(reader.getAttributeValue(null, "initialContextFactory"));
        loaderHelper.loadPolicySetsAndIntents(bd, reader, introspectionContext);
        if (uri != null) {
            while (true) {
                if (END_ELEMENT == reader.next() && "binding.jms".equals(reader.getName().getLocalPart())) {
                    return bd;
                }
            }
        }
        String name;
        while (true) {

            switch (reader.next()) {
            case START_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("destination".equals(name)) {
                    DestinationDefinition destination = loadDestination(reader);
                    metadata.setDestination(destination);
                } else if ("connectionFactory".equals(name)) {
                    ConnectionFactoryDefinition connectionFactory = loadConnectionFactory(reader);
                    metadata.setConnectionFactory(connectionFactory);
                } else if ("response".equals(name)) {
                    ResponseDefinition response = loadResponse(reader);
                    metadata.setResponse(response);
                } else if ("headers".equals(name)) {
                    HeadersDefinition headers = loadHeaders(reader, introspectionContext);
                    metadata.setHeaders(headers);
                } else if ("operationProperties".equals(name)) {
                    OperationPropertiesDefinition operationProperties = loadOperationProperties(reader, introspectionContext);
                    metadata.addOperationProperties(operationProperties.getName(), operationProperties);
                }
                break;
            case END_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("binding.jms".equals(name)) {
                    bd.setGeneratedTargetUri(loaderHelper.getURI(JmsLoaderHelper.generateURI(metadata)));
                    return bd;
                }
                break;
            }

        }

    }

    /*
     * Loads response definition.
     */
    private ResponseDefinition loadResponse(XMLStreamReader reader) throws XMLStreamException {

        ResponseDefinition response = new ResponseDefinition();

        String name;
        while (true) {

            switch (reader.next()) {
            case START_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("destination".equals(name)) {
                    DestinationDefinition destination = loadDestination(reader);
                    response.setDestination(destination);
                } else if ("connectionFactory".equals(name)) {
                    ConnectionFactoryDefinition connectionFactory = loadConnectionFactory(reader);
                    response.setConnectionFactory(connectionFactory);
                }
                break;
            case END_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("response".equals(name)) {
                    return response;
                }
                break;
            }

        }

    }

    /*
     * Loads connection factory definition.
     */
    private ConnectionFactoryDefinition loadConnectionFactory(XMLStreamReader reader) throws XMLStreamException {

        ConnectionFactoryDefinition connectionFactory = new ConnectionFactoryDefinition();

        connectionFactory.setName(reader.getAttributeValue(null, "name"));

        String create = reader.getAttributeValue(null, "create");
        if (create != null) {
            connectionFactory.setCreate(CreateOption.valueOf(create));
        }
        loadProperties(reader, connectionFactory, "connectionFactory");

        return connectionFactory;

    }

    /*
     * Loads destination definition.
     */
    private DestinationDefinition loadDestination(XMLStreamReader reader) throws XMLStreamException {

        DestinationDefinition destination = new DestinationDefinition();

        destination.setName(reader.getAttributeValue(null, "name"));

        String create = reader.getAttributeValue(null, "create");
        if (create != null) {
            destination.setCreate(CreateOption.valueOf(create));
        }

        String type = reader.getAttributeValue(null, "type");
        if (type != null) {
            destination.setDestinationType(DestinationType.valueOf(type));
        }

        loadProperties(reader, destination, "destination");

        return destination;

    }

    /*
     * Loads headers.
     */
    private HeadersDefinition loadHeaders(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        HeadersDefinition headers = new HeadersDefinition();
        headers.setJMSCorrelationId(reader.getAttributeValue(null, "JMSCorrelationId"));
        String deliveryMode = reader.getAttributeValue(null, "JMSDeliveryMode");
        if (deliveryMode != null) {
            try {
                headers.setJMSDeliveryMode(Integer.valueOf(deliveryMode));
            } catch (NumberFormatException nfe) {
                InvalidValue failure = new InvalidValue(deliveryMode + " is not a legal int value for JMSDeliveryMode", deliveryMode, reader, nfe);
                introspectionContext.addError(failure);
            }
        }
        String priority = reader.getAttributeValue(null, "JMSPriority");
        if (priority != null) {
            try {
                headers.setJMSPriority(Integer.valueOf(priority));
            } catch (NumberFormatException nfe) {
                InvalidValue failure = new InvalidValue(priority + " is not a legal int value for JMSPriority", priority, reader, nfe);
                introspectionContext.addError(failure);
            }
        }
        String timeToLive = reader.getAttributeValue(null, "JMSTimeToLive");
        if (timeToLive != null) {
            try {
                headers.setJMSTimeToLive(Long.valueOf(timeToLive));
            } catch (NumberFormatException nfe) {
                InvalidValue failure = new InvalidValue(timeToLive + " is not a legal int value for JMSTimeToLive", timeToLive, reader, nfe);
                introspectionContext.addError(failure);
            }
        }
        headers.setJMSType(reader.getAttributeValue(null, "JMSType"));
        loadProperties(reader, headers, "headers");
        return headers;
    }

    /*
     * Loads operation properties.
     */
    private OperationPropertiesDefinition loadOperationProperties(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        OperationPropertiesDefinition optProperties = new OperationPropertiesDefinition();
        optProperties.setName(reader.getAttributeValue(null, "name"));
        optProperties.setNativeOperation(reader.getAttributeValue(null, "nativeOperation"));
        String name;
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("headers".equals(name)) {
                    HeadersDefinition headersDefinition = loadHeaders(reader, introspectionContext);
                    optProperties.setHeaders(headersDefinition);
                } else if ("property".equals(name)) {
                    loadProperty(reader, optProperties);
                }
                break;
            case END_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("operationProperties".equals(name)) {
                    return optProperties;
                }
                break;
            }
        }

    }

    /*
     * Loads properties.
     */
    private void loadProperties(XMLStreamReader reader, PropertyAwareObject parent, String parentName) throws XMLStreamException {
        String name;
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                name = reader.getName().getLocalPart();
                if ("property".equals(name)) {
                    loadProperty(reader, parent);
                }
                break;
            case END_ELEMENT:
                name = reader.getName().getLocalPart();
                if (parentName.equals(name)) {
                    return;
                }
                break;
            }
        }
    }

    /**
     * Loads a property. TODO Support property type.
     */
    private void loadProperty(XMLStreamReader reader, PropertyAwareObject parent) throws XMLStreamException {
        final String key = reader.getAttributeValue(null, "name");
        final String value = reader.getElementText();
        parent.addProperty(key, value);
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
