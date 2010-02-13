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
package org.sca4j.fabric.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.fabric.services.expression.EnvironmentPropertyEvaluator;
import org.sca4j.fabric.services.expression.ExpressionExpanderImpl;
import org.sca4j.fabric.services.expression.SystemPropertyEvaluator;
import org.sca4j.host.expression.ExpressionEvaluator;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.services.xmlfactory.impl.XMLFactoryImpl;
import org.sca4j.spi.config.ConfigService;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.dom2java.generics.map.String2MapOfString2String;
import org.sca4j.transform.xml.Stream2Document;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigServiceImpl implements ConfigService {
    
    private static ConfigService INSTANCE = null;
    
    private Document domainConfig;
    private Map<String, String> hostProperties = new HashMap<String, String>();
    private String2MapOfString2String string2Properties = new String2MapOfString2String();
    private Stream2Document stream2Document = new Stream2Document();

    /**
     * Loads the domain-wide configuration.
     * 
     * @param configStream An input stream to config.
     * @return Singleton system config instance.
     */
    public synchronized static ConfigService getInstance(InputStream configStream) {
        if (INSTANCE == null) {
            INSTANCE = new ConfigServiceImpl(configStream);
        }
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getPropertyNames() {
        return hostProperties.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public Document getDomainConfig() {
        return domainConfig;
    }

    /**
     * {@inheritDoc}
     */
    public String getHostProperty(String name) {
        return hostProperties.get(name);
    }
    
    /*
     * Parses the user and system config.
     */
    private ConfigServiceImpl(InputStream configStream) {
        
        Map<Integer, ExpressionEvaluator> expressionEvaluators = new HashMap<Integer, ExpressionEvaluator>();
        expressionEvaluators.put(1, new EnvironmentPropertyEvaluator());
        expressionEvaluators.put(2, new SystemPropertyEvaluator());
        
        ExpressionExpanderImpl expressionExpander = new ExpressionExpanderImpl();
        expressionExpander.setEvaluators(expressionEvaluators);
        
        XMLFactory xmlFactory = new XMLFactoryImpl(expressionExpander);
        
        //parseUserConfig(xmlFactory);
        //parseSystemConfig(xmlFactory, configStream);
        
    }
    
    /*
     * Parses the user config.
     */
    private void parseUserConfig(XMLFactory xmlFactory) {
        File userConfigFile = new File(System.getProperty("user.home") + "/.sca4j/config.xml");
        try {
            if (userConfigFile.exists()) {
                InputStream inputStream = new FileInputStream(userConfigFile);
                parse(xmlFactory, inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            throw new ConfigServiceException(e.getMessage() + " parsing " + userConfigFile, e);
        }
    }
    
    /*
     * Parses the system config.
     */
    private void parseSystemConfig(XMLFactory xmlFactory, InputStream configStream) {
        try {
            if (configStream != null) {
                parse(xmlFactory, configStream);
                configStream.close();
            }
        } catch (Exception e) {
            throw new ConfigServiceException(e.getMessage() + " parsing system config", e);
        }
    }
    
    /*
     * Parses the system config.
     */
    private void parse(XMLFactory xmlFactory, InputStream inputStream) throws XMLFactoryInstantiationException, XMLStreamException, TransformationException {
            
        XMLStreamReader reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(inputStream);
        
        TransformContext context = new TransformContext();
        int next = reader.next();
        while (next != XMLStreamConstants.END_DOCUMENT) {
            switch (next) {
            case XMLStreamConstants.START_ELEMENT:
                String localName = reader.getLocalName();
                if ("hostProperties".equals(localName)) {
                    parseHostProperties(reader, context);
                } else if ("domainConfig".equals(localName)) {
                    parseDomainConfig(reader, context);
                }
            }
            next = reader.next();
        }
        
    }

    /*
     * Parses the host properties.
     */
    private void parseHostProperties(XMLStreamReader reader, TransformContext context) throws TransformationException {
        Document document = stream2Document.transform(reader, context);
        hostProperties.putAll(string2Properties.transform(document.getDocumentElement(), context));
    }

    /*
     * Parses the domain config.
     */
    private void parseDomainConfig(XMLStreamReader reader, TransformContext context) throws TransformationException {
        
        if (domainConfig == null) {
            domainConfig = stream2Document.transform(reader, context);
        } else {
            Element documentElement = domainConfig.getDocumentElement();
            NodeList nodeList = stream2Document.transform(reader, context).getDocumentElement().getChildNodes();
            for (int i = 0;i < nodeList.getLength();i++) {
                Node node = nodeList.item(i);
                Node firstNode = documentElement.getFirstChild();
                Node newNode = domainConfig.importNode(node, true);
                if (firstNode != null) documentElement.insertBefore(newNode, firstNode);
                else documentElement.appendChild(newNode);
            }
        }
        
    }

}
