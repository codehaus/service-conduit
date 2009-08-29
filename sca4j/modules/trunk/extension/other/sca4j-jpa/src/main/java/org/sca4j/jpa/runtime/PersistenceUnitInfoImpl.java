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
package org.sca4j.jpa.runtime;

import static org.sca4j.jpa.runtime.JpaConstants.CLASS;
import static org.sca4j.jpa.runtime.JpaConstants.EXCLUDE_UNLISTED_CLASSES;
import static org.sca4j.jpa.runtime.JpaConstants.JAR_FILE;
import static org.sca4j.jpa.runtime.JpaConstants.JTA_DATA_SOURCE;
import static org.sca4j.jpa.runtime.JpaConstants.MAPPING_FILE;
import static org.sca4j.jpa.runtime.JpaConstants.NON_JTA_DATA_SOURCE;
import static org.sca4j.jpa.runtime.JpaConstants.PROPERTY;
import static org.sca4j.jpa.runtime.JpaConstants.PROPERTY_NAME;
import static org.sca4j.jpa.runtime.JpaConstants.PROPERTY_VALUE;
import static org.sca4j.jpa.runtime.JpaConstants.PROVIDER;
import static org.sca4j.jpa.runtime.JpaConstants.TRANSACTION_TYPE;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sca4j.jpa.runtime.SCA4JJpaRuntimeException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encpasulates the information in the persistence.xml file.

 * This class is expected to be interogated by the provider only 
 * during the creation of the entity manager factory. Hence none 
 * of the values are cached, rather every time a property is queried 
 * the underlying DOM is interogated.
 *
 */
class PersistenceUnitInfoImpl implements PersistenceUnitInfo {

    /** Persistence DOM */
    private Node persistenceDom;

    /** Classloader */
    private ClassLoader classLoader;

    /** Root Url */
    private URL rootUrl;

    /** XPath API */
    private XPath xpath = XPathFactory.newInstance().newXPath();
    
    /** The name of the persistence unit wrapped by an instance of this type **/
    private String unitName;

    /**
     * Static factory method to be used instead of a directly called constructor
     * @param unitName
     * @param persistenceDom
     * @param classLoader
     * @param rootUrl
     * @return
     */
    public static PersistenceUnitInfoImpl getInstance(String unitName, Node persistenceDom, ClassLoader classLoader, URL rootUrl) {
    	PersistenceUnitInfoImpl matchedUnit = null;		
		List<String> persistenceUnitNames = getPersistenceUnitNames(persistenceDom);
		if(persistenceUnitNames.contains(unitName)) {
			matchedUnit = new PersistenceUnitInfoImpl(unitName, persistenceDom, classLoader, rootUrl);			
		}		

		return matchedUnit;    	
    }    
    
    /**
     * Initializes the properties.
     * @param unitName 
     * @param persistenceDom
     * @param classLoader
     * @param rootUrl
     */
    private PersistenceUnitInfoImpl(String unitName, Node persistenceDom, ClassLoader classLoader, URL rootUrl) {

        this.persistenceDom = persistenceDom;
        this.classLoader = classLoader;
        this.rootUrl = rootUrl;
        this.unitName = unitName;        
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#addTransformer(javax.persistence.spi.ClassTransformer)
     */
    public void addTransformer(ClassTransformer classTransformer) {
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#excludeUnlistedClasses()
     */
    public boolean excludeUnlistedClasses() {    	
        return getBooleanValue(persistenceDom, EXCLUDE_UNLISTED_CLASSES);
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getClassLoader()
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getJarFileUrls()
     */
    public List<URL> getJarFileUrls() {

        List<String> jarFiles = getMultipleValues(persistenceDom, JAR_FILE);

        try {
            List<URL> jarUrls = new LinkedList<URL>();
            for (String jarFile : jarFiles) {
                jarUrls.add(new URL(jarFile));
            }
            return jarUrls;
        } catch (MalformedURLException ex) {
            throw new SCA4JJpaRuntimeException(ex);
        }

    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getJtaDataSource()
     */
    public DataSource getJtaDataSource() {
        return null;
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getManagedClassNames()
     */
    public List<String> getManagedClassNames() {
        return getMultipleValues(persistenceDom, CLASS);
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getMappingFileNames()
     */
    public List<String> getMappingFileNames() {
        return getMultipleValues(persistenceDom, MAPPING_FILE);
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()
     */
    public ClassLoader getNewTempClassLoader() {
        return null;
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getNonJtaDataSource()
     */
    public DataSource getNonJtaDataSource() {
        return null;
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceProviderClassName()
     */
    public String getPersistenceProviderClassName() {
        return getSingleValue(persistenceDom, PROVIDER);
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitName()
     */
    public String getPersistenceUnitName() {
        return unitName;
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()
     */
    public URL getPersistenceUnitRootUrl() {
        return rootUrl;
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getProperties()
     */
    public Properties getProperties() {
        return getProperties(persistenceDom);
    }

    /**
     * @see javax.persistence.spi.PersistenceUnitInfo#getTransactionType()
     */
    public PersistenceUnitTransactionType getTransactionType() {

        String transactionType = getSingleValue(persistenceDom, TRANSACTION_TYPE);
        return "JTA".equals(transactionType) ? PersistenceUnitTransactionType.JTA : PersistenceUnitTransactionType.RESOURCE_LOCAL;

    }
    
    /**
     * @return Datasource name.
     */
    public String getDataSourceName() {
        
        String dataSourceName = getSingleValue(persistenceDom, JTA_DATA_SOURCE);
        if(dataSourceName == null) {
            dataSourceName = getSingleValue(persistenceDom, NON_JTA_DATA_SOURCE);
        }
        return dataSourceName;
        
    }

    /*
     * Extracts additional properties.
     */
    private Properties getProperties(Node root) {

        try {
        	String namedNodeExpression = getNamedNodeExpression(PROPERTY);
            NodeList nodeList = (NodeList) xpath.evaluate(namedNodeExpression, root, XPathConstants.NODESET);
            Properties data = new Properties();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element property = (Element) nodeList.item(i);
                data.put(property.getAttribute(PROPERTY_NAME), property.getAttribute(PROPERTY_VALUE));
            }
            return data;
        } catch (XPathExpressionException ex) {
            throw new SCA4JJpaRuntimeException(ex);
        }

    }

    /*
     * Gets multiple values for the specified expression.
     */
    private List<String> getMultipleValues(Node context, String expression) {

        try {
        	String namedNodeExpression = getNamedNodeExpression(expression);
            NodeList nodeList = (NodeList) xpath.evaluate(namedNodeExpression, context, XPathConstants.NODESET);
            List<String> data = new LinkedList<String>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                data.add(nodeList.item(i).getTextContent());
            }
            return data;
        } catch (XPathExpressionException ex) {
            throw new SCA4JJpaRuntimeException(ex);
        }

    }

    /*
     * Gets single value for the specified expression.
     */
    private String getSingleValue(Node context, String expression) {

        try {
        	String namedNodeExpression = getNamedNodeExpression(expression);
            String val = xpath.evaluate(namedNodeExpression, context);
            return "".equals(val) ? null : val;
        } catch (XPathExpressionException ex) {
            throw new SCA4JJpaRuntimeException(ex);
        }

    }

    /*
     * Gets single value for the specified expression.
     */
    private boolean getBooleanValue(Node context, String expression) {
        return Boolean.valueOf(getSingleValue(context, expression));
    }

    /**
     * Gets the xpath expression which provides the required value within a persistence unit of the given name 
     * @param expression the xpath expression for use within the named persistence unit
     * @return the input expression targeted for the named persistence unit
     */
    private String getNamedNodeExpression(String expression) {
		return MessageFormat.format(JpaConstants.NAMED_UNIT, unitName) + expression;
    }

    /**
     * gets the names of all of the persistence unit elements within the input node
     * @param searchBaseNode probably the base node of a persistence.xml document
     * @return a list of all persistence unit names
     */
	private static List<String> getPersistenceUnitNames(Node searchBaseNode) {    	
    	List<String> unitNames = new LinkedList<String>();
        try {
        	XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate(JpaConstants.ANY_UNIT + JpaConstants.NAME, searchBaseNode, XPathConstants.NODESET);            
            for (int i = 0; i < nodeList.getLength(); i++) {
            	unitNames.add(nodeList.item(i).getTextContent());
            }            
        } catch (XPathExpressionException ex) {
            throw new SCA4JJpaRuntimeException(ex);
        }
        
        return unitNames;
	}	    

}
