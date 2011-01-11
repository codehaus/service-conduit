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
package org.sca4j.binding.sftp.scdl;

import java.net.URI;

import org.sca4j.binding.sftp.common.SftpBindingMetadata;
import org.sca4j.binding.sftp.introspection.SftpBindingLoader;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * Binding definition for the SFTP binding loaded from the SCDL.
 * 
 */
public class SftpBindingDefinition extends BindingDefinition {    
    private static final long serialVersionUID = -889044951554792780L;
    private SftpBindingMetadata bindingMetadata;
    
    
    /**
     * Initialises the file binding definition
     *
     * @param uri          Target URI.
     * @param key          the key for mapped bindings
     */
    public SftpBindingDefinition(URI endpointUri, SftpBindingMetadata bindingMetadata, Document key) {
        super(endpointUri, SftpBindingLoader.BINDING_QNAME, key);
        this.bindingMetadata = bindingMetadata;
    }

    public SftpBindingMetadata getBindingMetadata() {
        return bindingMetadata;
    }
    
}