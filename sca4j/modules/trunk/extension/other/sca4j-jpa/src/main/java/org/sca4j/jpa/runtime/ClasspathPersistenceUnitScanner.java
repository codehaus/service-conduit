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
package org.sca4j.jpa.runtime;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @version $Revision$ $Date$
 */
public class ClasspathPersistenceUnitScanner implements PersistenceUnitScanner {

    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    private Map<String, PersistenceUnitInfo> persistenceUnitInfos = new HashMap<String, PersistenceUnitInfo>();
    private Set<URL> parsedUrls = new HashSet<URL>();

    /**
     * @see org.sca4j.jpa.runtime.PersistenceUnitScanner#getPersistenceUnitInfo(java.lang.String, java.lang.ClassLoader)
     */
    public PersistenceUnitInfo getPersistenceUnitInfo(String unitName, ClassLoader classLoader) {

        synchronized (persistenceUnitInfos) {

            if (persistenceUnitInfos.containsKey(unitName)) {
                return persistenceUnitInfos.get(unitName);
            }

            try {

                DocumentBuilder db = dbf.newDocumentBuilder();

                // JEE 5 servers seem to auto-deploy persistence units
                Enumeration<URL> persistenceUnitUrls = classLoader.getResources("META-INF/persistence.xml");
                
                while (persistenceUnitUrls.hasMoreElements()) {

                    URL persistenceUnitUrl = persistenceUnitUrls.nextElement();
                    if (parsedUrls.contains(persistenceUnitUrl)) {
                    	continue;
                    }
                    Document persistenceDom = db.parse(persistenceUnitUrl.openStream());
                    URL rootUrl = getRootJarUrl(persistenceUnitUrl);
                    for (PersistenceUnitInfo info : PersistenceUnitInfoImpl.parse(persistenceDom, classLoader, rootUrl)) {
                    	persistenceUnitInfos.put(info.getPersistenceUnitName(), info);
                    }
                    parsedUrls.add(persistenceUnitUrl);
                    if (persistenceUnitInfos.containsKey(unitName)) {
                        return persistenceUnitInfos.get(unitName);
                    }
                    
                }
                
                persistenceUnitUrls = classLoader.getResources("META-INF/sca4j-persistence.xml");
                
                while (persistenceUnitUrls.hasMoreElements()) {

                    URL persistenceUnitUrl = persistenceUnitUrls.nextElement();
                    if (parsedUrls.contains(persistenceUnitUrl)) {
                        continue;
                    }
                    Document persistenceDom = db.parse(persistenceUnitUrl.openStream());
                    URL rootUrl = getRootJarUrl(persistenceUnitUrl);
                    for (PersistenceUnitInfo info : PersistenceUnitInfoImpl.parse(persistenceDom, classLoader, rootUrl)) {
                        persistenceUnitInfos.put(info.getPersistenceUnitName(), info);
                    }
                    parsedUrls.add(persistenceUnitUrl);
                    if (persistenceUnitInfos.containsKey(unitName)) {
                        return persistenceUnitInfos.get(unitName);
                    }
                    
                }

            } catch (IOException ex) {
                throw new SCA4JJpaRuntimeException(ex);
            } catch (ParserConfigurationException ex) {
                throw new SCA4JJpaRuntimeException(ex);
            } catch (SAXException ex) {
                throw new SCA4JJpaRuntimeException(ex);
            }

        }

        throw new SCA4JJpaRuntimeException("Unable to find persistence unit: " + unitName);

    }

    private URL getRootJarUrl(URL persistenceUnitUrl) throws IOException {
		String protocol = persistenceUnitUrl.getProtocol();

		if ("jar".equals(protocol)) {
			JarURLConnection jarURLConnection = (JarURLConnection) persistenceUnitUrl
					.openConnection();
			return jarURLConnection.getJarFileURL();
		} else if ("file".equals(protocol)) {
			String path = persistenceUnitUrl.getPath();
			return new File(path).getParentFile().getParentFile().toURI().toURL();
		} else if ("zip".equals(protocol)) {
			String path = persistenceUnitUrl.getPath();
			String rootJarUrl = path.substring(0,path.lastIndexOf("META-INF") - 2);
			rootJarUrl = "file:" + rootJarUrl;
			return new URL(rootJarUrl);
		} else {
			throw new SCA4JJpaRuntimeException("Unable to handle protocol: "	+ protocol);
		}
	}


}
