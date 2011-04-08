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
package org.sca4j.binding.file.scdl;

import java.net.URI;

import org.sca4j.binding.file.common.FileBindingMetadata;
import org.sca4j.binding.file.introspection.FileBindingLoader;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * Binding definition for the File binding loaded from the SCDL.
 * 
 */
public class FileBindingDefinition extends BindingDefinition {    
    private static final long serialVersionUID = -889044951554792780L;
    private FileBindingMetadata bindingMetadata;
    
    
    /**
     * Initialises the file binding definition
     *
     * @param uri          Target URI.
     * @param key          the key for mapped bindings
     */
    public FileBindingDefinition(URI endpointUri, FileBindingMetadata bindingMetadata, Document key) {
        super(endpointUri, FileBindingLoader.BINDING_QNAME, key);
        this.bindingMetadata = bindingMetadata;
    }

    public FileBindingMetadata getBindingMetadata() {
        return bindingMetadata;
    }
    
}