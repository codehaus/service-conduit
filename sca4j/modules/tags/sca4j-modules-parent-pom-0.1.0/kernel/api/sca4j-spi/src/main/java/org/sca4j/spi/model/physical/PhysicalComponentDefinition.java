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
package org.sca4j.spi.model.physical;

import java.net.URI;

/**
 * Represents a physical component model.
 *
 * @version $Rev: 3715 $ $Date: 2008-04-24 18:54:02 +0100 (Thu, 24 Apr 2008) $
 */
public abstract class PhysicalComponentDefinition {

    private URI componentId;
    private String scope;
    private URI groupId;
    private int initLevel;
    private long maxIdleTime;
    private long maxAge;

    /**
     * Gets the component id.
     *
     * @return Component id.
     */
    public URI getComponentId() {
        return componentId;
    }

    /**
     * Sets the component id.
     *
     * @param componentId the component id
     */
    public void setComponentId(URI componentId) {
        this.componentId = componentId;
    }

    /**
     * Returns the id of the component group this component belongs to.
     *
     * @return the id of the component group this component belongs to
     */
    public URI getGroupId() {
        return groupId;
    }

    /**
     * Sets the id of the component group this component belongs to.
     *
     * @param groupId the id of the component group this component belongs to
     */
    public void setGroupId(URI groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns the component initialization level.
     *
     * @return the component initialization level
     */
    public int getInitLevel() {
        return initLevel;
    }

    /**
     * Sets the component initialization level.
     *
     * @param initLevel the component initialization level.
     */
    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
    }

    /**
     * Gets the scope for the component.
     *
     * @return The scope for the component.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the component.
     *
     * @param scope The scope for the component.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the idle time allowed between operations in milliseconds if the implementation is conversational.
     *
     * @return the idle time allowed between operations in milliseconds if the implementation is conversational
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * Sets the idle time allowed between operations in milliseconds if the implementation is conversational.
     *
     * @param maxIdleTime the idle time
     */
    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    /**
     * Returns the maximum age a conversation may remain active in milliseconds if the implementation is conversational.
     *
     * @return the maximum age a conversation may remain active in milliseconds if the implementation is conversational
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the maximum age a conversation may remain active in milliseconds if the implementation is conversational.
     *
     * @param maxAge the maximum age
     */
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        
        PhysicalComponentDefinition other = (PhysicalComponentDefinition) obj;
        return super.equals(componentId.equals(other.getComponentId()));
        
    }

    @Override
    public int hashCode() {
        return componentId.hashCode();
    }
    
    @Override
    public String toString() {
        return componentId.toString();
    }
}
