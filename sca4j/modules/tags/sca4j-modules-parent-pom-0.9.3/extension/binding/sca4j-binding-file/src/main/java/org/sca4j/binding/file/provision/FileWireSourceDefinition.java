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
 */
package org.sca4j.binding.file.provision;

import org.sca4j.binding.file.common.FileBindingMetadata;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;

/**
 * Definition of the physical source of the <code>binding.file</code>.
 * 
 */
public class FileWireSourceDefinition extends PhysicalWireSourceDefinition {
    private final FileBindingMetadata bindingMetaData;

    public FileWireSourceDefinition(FileBindingMetadata bindingMetaData) {
        this.bindingMetaData = bindingMetaData;
    }

    public FileBindingMetadata getBindingMetaData() {
        return bindingMetaData;
    }
}
