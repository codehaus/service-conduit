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
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
package org.sca4j.pojo.provision;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import org.sca4j.spi.model.physical.PhysicalComponentDefinition;

/**
 * Definition of a physical component whose actual implementation is based on a POJO.
 *
 * @version $Rev: 5250 $ $Date: 2008-08-21 02:18:25 +0100 (Thu, 21 Aug 2008) $
 */
public abstract class PojoComponentDefinition extends PhysicalComponentDefinition {

    private InstanceFactoryDefinition providerDefinition;
    private URI classLoaderId;
    private final Map<String, Document> propertyValues = new HashMap<String, Document>();

    /**
     * Gets the instance factory provider definition.
     *
     * @return Instance factory provider definition.
     */
    public InstanceFactoryDefinition getProviderDefinition() {
        return providerDefinition;
    }

    /**
     * Sets the instance factory provider definition.
     *
     * @param providerDefinition
     *         Instance factory provider definition.
     */
    public void setProviderDefinition(InstanceFactoryDefinition providerDefinition) {
        this.providerDefinition = providerDefinition;
    }

    /**
     * Gets the classloader id.
     *
     * @return Classloader id.
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Set the classloader id.
     *
     * @param classLoaderId Classloader id.
     */
    public void setClassLoaderId(URI classLoaderId) {
        this.classLoaderId = classLoaderId;
    }

    /**
     * Return all property values.
     *
     * @return a Map containing all property values keyed by name
     */
    public Map<String, Document> getPropertyValues() {
        return propertyValues;
    }

    /**
     * Return the value of the property with the supplied name.
     *
     * @param name the name of the property
     * @return the property's value
     */
    public Document getPropertyValue(String name) {
        return propertyValues.get(name);
    }

    /**
     * Sets the value for a property.
     *
     * @param name  the name of the property
     * @param value its value
     */
    public void setPropertyValue(String name, Document value) {
        propertyValues.put(name, value);
    }
}
