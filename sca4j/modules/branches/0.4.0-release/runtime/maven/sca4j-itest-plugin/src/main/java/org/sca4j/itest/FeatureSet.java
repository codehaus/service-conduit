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
package org.sca4j.itest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Dependency;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @version $Revision$ $Date$
 */
public class FeatureSet {
    
    private Set<Dependency> extensions = new HashSet<Dependency>();
    private Set<Dependency> sharedLibraries = new HashSet<Dependency>();
    
    
    /**
     * Adds an extension to the feature set.
     * 
     * @param extension Extension to be added to the feature set.
     */
    public void addExtension(Dependency extension) {
        extensions.add(extension);
    }
    
    
    /**
     * Adds a shared library to the feature set.
     * 
     * @param shared Shared library to be added to the feature set.
     */
    public void addSharedLibrary(Dependency sharedLibrary) {
    	sharedLibraries.add(sharedLibrary);
    }
    
    /**
     * Serializes the feature set to the deployable artifact file.
     * 
     * @param artifactFile File to which the feture set needs to be written.
     * @throws FileNotFoundException 
     */
    public void serialize(File artifactFile) throws FileNotFoundException {
        
        PrintWriter writer = null;
        
        try {
            
            writer = new PrintWriter(new FileOutputStream(artifactFile));
            
            writer.println("<featureSet>");
            for (Dependency extension : extensions) {
                writer.println("    <extension>");
                writer.println("        <artifactId>" + extension.getArtifactId() + "</artifactId>");
                writer.println("        <groupId>" + extension.getGroupId() + "</groupId>");
                writer.println("        <version>" + extension.getVersion() + "</version>");
                writer.println("    </extension>");
            }
            for (Dependency sharedLibrary : sharedLibraries) {
                writer.println("    <shared>");
                writer.println("        <artifactId>" + sharedLibrary.getArtifactId() + "</artifactId>");
                writer.println("        <groupId>" + sharedLibrary.getGroupId() + "</groupId>");
                writer.println("        <version>" + sharedLibrary.getVersion() + "</version>");
                writer.println("    </shared>");
            }
            writer.println("</featureSet>");
            writer.flush();
            
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        
    }
    
    public static FeatureSet deserialize(File featureSetFile) throws ParserConfigurationException, SAXException, IOException {
    	
    	FeatureSet featureSet = new FeatureSet();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document featureSetDoc = db.parse(featureSetFile);

        NodeList extensionList = featureSetDoc.getElementsByTagName("extension");
        for (int i = 0; i < extensionList.getLength(); i++) {
            Dependency extension = createDependency(extensionList, i);
            featureSet.addExtension(extension);
        }

        NodeList sharedList = featureSetDoc.getElementsByTagName("shared");
        for (int i = 0; i < sharedList.getLength(); i++) {
            Dependency sharedLibrary = createDependency(sharedList, i);
            featureSet.addSharedLibrary(sharedLibrary);
        }
        
        return featureSet;
    }


	private static Dependency createDependency(NodeList extensionList, int i) {
		
		Element extensionElement = (Element) extensionList.item(i);

		Element artifactIdElement = (Element) extensionElement.getElementsByTagName("artifactId").item(0);
		Element groupIdElement = (Element) extensionElement.getElementsByTagName("groupId").item(0);
		Element versionElement = (Element) extensionElement.getElementsByTagName("version").item(0);

		Dependency extension = new Dependency();
		extension.setArtifactId(artifactIdElement.getTextContent());
		extension.setGroupId(groupIdElement.getTextContent());
		extension.setVersion(versionElement.getTextContent());
		
		return extension;
		
	}

	public Set<Dependency> getSharedLibraries() {
		return sharedLibraries;
	}

	public Set<Dependency> getExtensions() {
		return extensions;
	}

}
