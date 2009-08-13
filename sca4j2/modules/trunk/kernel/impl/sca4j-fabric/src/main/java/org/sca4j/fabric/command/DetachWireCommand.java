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

package org.sca4j.fabric.command;

import java.util.Set;
import java.util.LinkedHashSet;

import org.sca4j.spi.command.AbstractCommand;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;

/*
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
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

public class DetachWireCommand extends AbstractCommand {

    private final Set<PhysicalWireDefinition> physicalWireDefinitions =
            new LinkedHashSet<PhysicalWireDefinition>();

    public DetachWireCommand(int order) {
        super(order);
    }

    public Set<PhysicalWireDefinition> getPhysicalWireDefinitions() {
        return physicalWireDefinitions;
    }

    public void addPhysicalWireDefinition(PhysicalWireDefinition physicalWireDefinition) {
        physicalWireDefinitions.add(physicalWireDefinition);
    }

    public void addPhysicalWireDefinitions(Set<PhysicalWireDefinition> physicalWireDefinitions) {
        this.physicalWireDefinitions.addAll(physicalWireDefinitions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        try {
            DetachWireCommand other = (DetachWireCommand) obj;
            return physicalWireDefinitions.equals(other.physicalWireDefinitions);   
        } catch (ClassCastException cce) {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return physicalWireDefinitions.hashCode();
    }

}

