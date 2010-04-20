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
package org.sca4j.idl.wsdl.processor.extension;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;

import org.sca4j.spi.services.contribution.Resource;

/**
 * Used for handling any extensions to the WSDL.
 * 
 * @author meerajk
 *
 */
public interface ExtensionHandler<E extends ExtensibilityElement> extends ExtensionSerializer, ExtensionDeserializer {
    
    /**
     * Gets the qualified name of the extensibility element.
     * 
     * @return Qualifid name of the extensibility element.
     */
    QName getQualifiedName();
    
    /**
     * Callback when the extensibility element is indexed.
     * 
     * @param extensibilityElement Desrialised extensibility element.
     * @param resource Resoure corresponding to the WSDL document.
     */
    void indexExtension(E extensibilityElement, Resource resource);
    
    /**
     * Callback when the extensibility element is processed.
     * 
     * @param extensibilityElement Desrialised extensibility element.
     * @param resource Resoure corresponding to the WSDL document.
     */
    void processExtension(E extensibilityElement, Resource resource);

}
