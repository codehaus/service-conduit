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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.sca4j.transform.xml.Stream2Document;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigServiceImpl implements ConfigService {
    
    private Document domainConfig;
    private Map<String, String> hostProperties = new HashMap<String, String>();
    private Stream2Document stream2Document = new Stream2Document();

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
    public ConfigServiceImpl(InputStream configStream) {
        
        initializeConfig();
        
        Map<Integer, ExpressionEvaluator> expressionEvaluators = new HashMap<Integer, ExpressionEvaluator>();
        expressionEvaluators.put(1, new EnvironmentPropertyEvaluator());
        expressionEvaluators.put(2, new SystemPropertyEvaluator());
        
        ExpressionExpanderImpl expressionExpander = new ExpressionExpanderImpl();
        expressionExpander.setEvaluators(expressionEvaluators);
        
        XMLFactory xmlFactory = new XMLFactoryImpl(expressionExpander);
        
        parseUserConfig(xmlFactory);
        parseSystemConfig(xmlFactory, configStream);
        
    }

    /*
     * Initialises the config.
     */
    private void initializeConfig() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            domainConfig  = factory.newDocumentBuilder().newDocument();
            Element root = domainConfig.createElement("config");
            domainConfig.appendChild(root);
        } catch (ParserConfigurationException e) {
            throw new ConfigServiceException("Unable to create JAXP parser", e);
        }
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
        
        while (reader.next() != XMLStreamConstants.START_ELEMENT) {
        }
        
        Element documentElement = domainConfig.getDocumentElement();
        NodeList nodeList = stream2Document.transform(reader, context).getDocumentElement().getChildNodes();
        for (int i = 0;i < nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            if ("host.properties".equals(node.getNodeName())) {
                NodeList propertyList = node.getChildNodes();
                for (int j = 0;j < propertyList.getLength();j++) {
                    Node propertyNode = propertyList.item(j);
                    if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element propertyElement = (Element) propertyNode;
                        hostProperties.put(propertyElement.getTagName(), propertyElement.getTextContent());
                    }
                }
                
            } else {
                Node firstNode = documentElement.getFirstChild();
                Node newNode = domainConfig.importNode(node, true);
                if (firstNode != null) documentElement.insertBefore(newNode, firstNode);
                else documentElement.appendChild(newNode);
            }
        }
        
    }

}
