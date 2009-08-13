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
package org.sca4j.jpa.runtime;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sca4j.jpa.runtime.SCA4JJpaRuntimeException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @version $Revision$ $Date$
 */
public class ClasspathPersistenceUnitScanner implements PersistenceUnitScanner {

    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    private Map<String, PersistenceUnitInfo> persistenceUnitInfos = new HashMap<String, PersistenceUnitInfo>();

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

                Enumeration<URL> persistenceUnitUrls = classLoader.getResources("META-INF/persistence.xml");

                while (persistenceUnitUrls.hasMoreElements()) {

                    URL persistenceUnitUrl = persistenceUnitUrls.nextElement();
                    Document persistenceDom = db.parse(persistenceUnitUrl.openStream());
                    URL rootUrl = getRootJarUrl(persistenceUnitUrl);

                    PersistenceUnitInfo info = PersistenceUnitInfoImpl.getInstance(unitName, persistenceDom, classLoader, rootUrl);
                    if (info != null) {
                        persistenceUnitInfos.put(unitName, info);
                        return info;
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
