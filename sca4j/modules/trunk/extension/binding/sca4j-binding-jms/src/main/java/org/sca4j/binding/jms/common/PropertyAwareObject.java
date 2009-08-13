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
package org.sca4j.binding.jms.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sca4j.scdl.ModelObject;

public abstract class PropertyAwareObject extends ModelObject {
    private static final long serialVersionUID = 7862305926561642783L;
    private Map<String, String> properties = null;

    /**
     * Sets the properties used to create the administered object.
     * 
     * @param properties used to create the administered object.
     */
    public void setProperties(Map<String, String> properties) {
        ensurePropertiesNotNull();
        this.properties.putAll(properties);
    }

    /**
     * Add a Property.
     * 
     * @param name Name of the property.
     * @param value Value of the property.
     */
    public void addProperty(String name, String value) {
        ensurePropertiesNotNull();
        properties.put(name, value);
    }

    private void ensurePropertiesNotNull() {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
    }

    /**
     * Returns properties used to create the administered object.
     * 
     * @return Properties used to create the administered object.
     */
    public Map<String, String> getProperties() {
        if (this.properties != null) {
            return properties;
        } else {
            return Collections.emptyMap();
        }
    }
}
