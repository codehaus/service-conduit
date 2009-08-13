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
package org.sca4j.binding.aq.provision;

import java.net.URI;

import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * @version $Revision: 4817 $ $Date: 2008-06-11 20:01:35 +0100 (Wed, 11 Jun 2008) $
 */
public class AQWireTargetDefinition extends PhysicalWireTargetDefinition {
    
    private final String destinationName;
    private final String dataSourceKey;
    private final URI classLoaderId;
    
    /**
     * Constructs the Wire Definition
     * @param destinationName
     * @param dataSourceKey
     * @param classLoaderId
     */
    public AQWireTargetDefinition(String destinationName, String dataSourceKey, URI classLoaderId) {
        this.destinationName = destinationName;
        this.dataSourceKey = dataSourceKey;
        this.classLoaderId = classLoaderId;
    }

    /**
     * Get Destination Name
     * @return
     */
    public String getDestinationName() {
        return destinationName;
    }
    
    /**
     * Gets the Data Source Key
     * @return
     */
    public String getDataSourceKey() {
        return dataSourceKey;
    }

    /**
     * Gets the Classloader URI
     * @return
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

}
