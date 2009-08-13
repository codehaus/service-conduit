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
package org.sca4j.jpa.scdl;

import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.scdl.ServiceContract;

/**
 * Represents an entity manager factory treated as a resource.
 *
 * @version $Revision$ $Date$
 */
public final class PersistenceUnitResource extends ResourceDefinition {
    private static final long serialVersionUID = 8935762119919982256L;
    private final String unitName;

    /**
     * Initializes the resource name and persistence unit name.
     * 
     * @param name Name of the resource.
     * @param unitName Persistence unit name.
     * @param serviceContract the service contract for the persistence unit
     */
    public PersistenceUnitResource(String name, String unitName, ServiceContract<?> serviceContract) {
        super(name, serviceContract, true);
        this.unitName = unitName;
    }
    
    /**
     * Gets the persistence unit name.
     * @return Persistence unit name.
     */
    public final String getUnitName() {
        return this.unitName;
    }

}
