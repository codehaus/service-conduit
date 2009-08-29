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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.sca4j.jpa.runtime.PersistenceUnitInfoImpl;
import org.w3c.dom.Document;

public class PersistenceUnitInfoImplTestCase extends TestCase {
	
	private static final String PERSISTENCE_PROVIDER = "PERSISTENCE_PROVIDER";
	private static final String UNIT_NAME = "UNIT_NAME";
	private static final String TRANS_TYPE = "TRANS_TYPE";	
	private static final String DS_NAME = "DS_NAME";	

	public void testFirstOfMultiple() throws Exception {
		
        URL persistenceUnitUrl = getPersistenceUnitUrl();        
        
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document persistenceDom = db.parse(persistenceUnitUrl.openStream());
        
        final String expectedUnitName = "test";
        Map<String, String> expectedSimpleValues = new HashMap<String, String>();        
        expectedSimpleValues.put(UNIT_NAME, expectedUnitName);
        expectedSimpleValues.put(PERSISTENCE_PROVIDER, "org.apache.openjpa.persistence.PersistenceProviderImpl");
        expectedSimpleValues.put(TRANS_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.toString());
        expectedSimpleValues.put(DS_NAME, null);
        
        HashSet<String> expectedEntityClasses = new HashSet<String>();
        expectedEntityClasses.add("org.sca4j.jpa.Employee");
        
        Properties expectedProperties = new Properties();
        expectedProperties.put("openjpa.ConnectionURL" ,"jdbc:hsqldb:tutorial_database");
        expectedProperties.put("openjpa.ConnectionDriverName","org.hsqldb.jdbcDriver");
        expectedProperties.put("openjpa.ConnectionUserName", "sa");
        expectedProperties.put("openjpa.ConnectionPassword","");
        expectedProperties.put("openjpa.Log","DefaultLevel=WARN, Tool=INFO");
        
		PersistenceUnitInfoImpl matchedUnit = PersistenceUnitInfoImpl.getInstance(expectedUnitName, persistenceDom, getClass().getClassLoader(), persistenceUnitUrl);

		assertState(matchedUnit, expectedSimpleValues, expectedEntityClasses, expectedProperties);		
	}
	
	public void testLastOfMultiple() throws Exception {
		
        URL persistenceUnitUrl = getPersistenceUnitUrl();                
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document persistenceDom = db.parse(persistenceUnitUrl.openStream());        
        
        final String expectedUnitName = "testThree";
        Map<String, String> expectedSimpleValues = new HashMap<String, String>();        
        expectedSimpleValues.put(UNIT_NAME, expectedUnitName);
        expectedSimpleValues.put(PERSISTENCE_PROVIDER, "org.test.ProviderNameThree");
        expectedSimpleValues.put(TRANS_TYPE, PersistenceUnitTransactionType.JTA.toString());
        expectedSimpleValues.put(DS_NAME, "EmployeeDSThree");
        
        HashSet<String> expectedEntityClasses = new HashSet<String>();
        expectedEntityClasses.add("org.sca4j.jpa.model.Employee");
        expectedEntityClasses.add("org.sca4j.jpa.model.Employee2");
        expectedEntityClasses.add("org.sca4j.jpa.model.Employee3");
        expectedEntityClasses.add("org.sca4j.jpa.model.Employee4");
        
        Properties expectedProperties = new Properties();
        expectedProperties.put("hibernate.dialect" ,"org.hibernate.test.dialect.Three");
        expectedProperties.put("hibernate.transaction.manager_lookup_class","org.sca4j.jpa.hibernate.SCA4JHibernateTransactionManagerLookupThree");
        expectedProperties.put("hibernate.hbm2ddl.auto", "create-drop-three");
        
        PersistenceUnitInfoImpl matchedUnit = PersistenceUnitInfoImpl.getInstance(expectedUnitName, persistenceDom, getClass().getClassLoader(), persistenceUnitUrl);

		assertState(matchedUnit, expectedSimpleValues, expectedEntityClasses, expectedProperties);		
	}
	
	public void testMiddleOfMultiple() throws Exception {
		
        URL persistenceUnitUrl = getPersistenceUnitUrl();        
        
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document persistenceDom = db.parse(persistenceUnitUrl.openStream());
        
        final String expectedUnitName = "testTwo";
        Map<String, String> expectedSimpleValues = new HashMap<String, String>();        
        expectedSimpleValues.put(UNIT_NAME, expectedUnitName);
        expectedSimpleValues.put(PERSISTENCE_PROVIDER, "org.test.ProviderNameTwo");
        expectedSimpleValues.put(TRANS_TYPE, PersistenceUnitTransactionType.JTA.toString());
        expectedSimpleValues.put(DS_NAME, "EmployeeDSTwo");
        
        HashSet<String> expectedEntityClasses = new HashSet<String>();
        expectedEntityClasses.add("org.sca4j.jpa.model.Employee");
        expectedEntityClasses.add("org.sca4j.jpa.model.Employee2");
                
        Properties expectedProperties = new Properties();
        expectedProperties.put("hibernate.dialect" ,"org.hibernate.test.dialect.Two");
        expectedProperties.put("hibernate.transaction.manager_lookup_class","org.sca4j.jpa.hibernate.SCA4JHibernateTransactionManagerLookupTwo");
        expectedProperties.put("hibernate.hbm2ddl.auto", "create-drop-two");
        
        PersistenceUnitInfoImpl matchedUnit = PersistenceUnitInfoImpl.getInstance(expectedUnitName, persistenceDom, getClass().getClassLoader(), persistenceUnitUrl);

		assertState(matchedUnit, expectedSimpleValues, expectedEntityClasses, expectedProperties);		
	}
	
	

	private void assertState(PersistenceUnitInfoImpl matchedUnit, Map<String, String> expectedResults, HashSet<String> expectedEntityClasses, Properties expectedProperties) {
		assertEquals(expectedResults.get(UNIT_NAME), matchedUnit.getPersistenceUnitName());
		assertEquals(expectedResults.get(PERSISTENCE_PROVIDER), matchedUnit.getPersistenceProviderClassName());	
		assertEquals(expectedResults.get(TRANS_TYPE), matchedUnit.getTransactionType().toString());
		assertEquals(expectedResults.get(DS_NAME), matchedUnit.getDataSourceName());
		
		//Order insensitive comparison of the entity class names
		HashSet<String> actualEntityClasses = new HashSet<String>(matchedUnit.getManagedClassNames());		
		assertTrue(expectedEntityClasses.equals(actualEntityClasses));
		
		assertTrue(expectedProperties.equals(matchedUnit.getProperties()));
	}
	
	private URL getPersistenceUnitUrl() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();		
        Enumeration<URL> persistenceUnitUrls = classLoader.getResources("META-INF/persistence.xml");
		
		//One and only one persistence unit resource match is expected for the tests
        assertTrue(persistenceUnitUrls.hasMoreElements());        
        URL persistenceUnitUrl = persistenceUnitUrls.nextElement();
        assertFalse(persistenceUnitUrls.hasMoreElements());
		return persistenceUnitUrl;
	}	
}
