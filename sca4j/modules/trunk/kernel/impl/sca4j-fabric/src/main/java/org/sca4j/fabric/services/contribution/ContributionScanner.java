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
package org.sca4j.fabric.services.contribution;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.JarContributionSource;

public class ContributionScanner {

    /*
     * Gets the list of extension contributions.
     */
    public ContributionSource[] scanExtensionContributions() {
        return scanContributions(true);
    }

    /*
     * Gets the list of user contributions.
     */
    public ContributionSource[] scanUserContributions() {
        return scanContributions(false);
    }

    /*
     * Gets the list of contributions.
     */
    private ContributionSource[] scanContributions(boolean extension) {

        try {
            List<ContributionSource> extensions = new LinkedList<ContributionSource>();
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/sca-contribution.xml");

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (isExtension(resource) == extension) {
                    if ("zip".equals(resource.getProtocol())) {
                        // stupid weblogic
                        resource = new URL(resource.toExternalForm().replaceFirst("^zip:", "jar:file:"));
                    }
                    if ("jar".equals(resource.getProtocol())) {
                        JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                        URL resourceUrl = jarURLConnection.getJarFileURL();
                        extensions.add(new JarContributionSource(resourceUrl, -1));
                    }
                }
            }
            return extensions.toArray(new ContributionSource[extensions.size()]);
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (XMLStreamException e) {
            throw new AssertionError(e);
        }

    }

    /*
     * Checks whether the contribution is an extension.
     */
    private boolean isExtension(URL url) throws IOException, XMLStreamException {

        XMLStreamReader reader = null;
        InputStream stream = null;
        try {
            stream = url.openStream();
            reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
            reader.nextTag();
            return Boolean.valueOf(reader.getAttributeValue(null, "extension"));
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

}
