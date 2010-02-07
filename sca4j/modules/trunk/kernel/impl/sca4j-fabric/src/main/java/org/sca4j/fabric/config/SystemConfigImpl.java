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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import org.sca4j.spi.config.SystemConfig;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.dom2java.generics.map.String2MapOfString2String;
import org.sca4j.transform.xml.Stream2Document;
import org.w3c.dom.Document;

public class SystemConfigImpl implements SystemConfig {
    
    private static SystemConfig INSTANCE = null;
    
    private Document domainConfig;
    private Map<String, String> hostProperties;
    
    /**
     * Loads the system config.
     * @param inputStream Data stream that contains the config.
     */
    private SystemConfigImpl(InputStream inputStream) {
        
        Map<Integer, ExpressionEvaluator> expressionEvaluators = new HashMap<Integer, ExpressionEvaluator>();
        expressionEvaluators.put(1, new EnvironmentPropertyEvaluator());
        expressionEvaluators.put(1, new SystemPropertyEvaluator());
        
        ExpressionExpanderImpl expressionExpander = new ExpressionExpanderImpl();
        expressionExpander.setEvaluators(expressionEvaluators);
        
        XMLFactory xmlFactory = new XMLFactoryImpl(expressionExpander);
        parse(xmlFactory, inputStream);
        
    }

    /**
     * Loads the system config.
     * @param inputStream Data stream that contains the config.
     * @return Singleton system config instance.
     */
    public synchronized static SystemConfig getInstance(InputStream inputStream) {
        if (INSTANCE == null) {
            INSTANCE = new SystemConfigImpl(inputStream);
        }
        return INSTANCE;
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
     * Parses the system config.
     */
    private void parse(XMLFactory xmlFactory, InputStream inputStream) {
        
        try {
            
            XMLStreamReader reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(inputStream);
        
            String2MapOfString2String string2Properties = new String2MapOfString2String();
            Stream2Document stream2Document = new Stream2Document();
            
            TransformContext context = new TransformContext();
            int next = reader.next();
            while (next != XMLStreamConstants.END_DOCUMENT) {
                switch (next) {
                case XMLStreamConstants.START_ELEMENT:
                    String localName = reader.getLocalName();
                    if ("hostProperties".equals(localName)) {
                        Document document = stream2Document.transform(reader, context);
                        hostProperties = string2Properties.transform(document.getDocumentElement(), context);
                    } else if ("domainConfig".equals(localName)) {
                        domainConfig = stream2Document.transform(reader, context);
                    }
                }
                next = reader.next();
            }
            
        } catch (XMLFactoryInstantiationException e) {
            throw new AssertionError(e);
        } catch (XMLStreamException e) {
            throw new AssertionError(e);
        } catch (TransformationException e) {
            throw new AssertionError(e);
        }
        
    }

}
