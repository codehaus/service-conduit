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
package org.sca4j.bpel.provision;

import java.net.URI;

import javax.xml.namespace.QName;

import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;

/**
 * Represents a physical BPEL wire source definition.
 * 
 * @author meerajk
 *
 */
public class BpelPhysicalWireSourceDefinition extends PhysicalWireSourceDefinition {
    
    private String partnerLinkName;
    private QName processName;
    private URI componentId;

    public BpelPhysicalWireSourceDefinition(String partnerLinkName, QName processName, URI componentId) {
        this.partnerLinkName = partnerLinkName;
        this.processName = processName;
        this.componentId = componentId;
    }
    
    public String getPartnerLinkName() {
        return partnerLinkName;
    }
    
    public QName getProcessName() {
        return processName;
    }
    
    public URI getComponentId() {
        return componentId;
    }

    @Override
    public boolean isOptimizable() {
        return false;
    }

}
