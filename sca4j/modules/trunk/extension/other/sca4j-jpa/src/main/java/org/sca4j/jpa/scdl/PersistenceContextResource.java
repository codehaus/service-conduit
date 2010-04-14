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
package org.sca4j.jpa.scdl;

import javax.persistence.PersistenceContextType;

import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.scdl.ServiceContract;

/**
 * Represents an entity manager factory treated as a resource.
 *
 * @version $Revision$ $Date$
 */
public final class PersistenceContextResource extends ResourceDefinition {
    private static final long serialVersionUID = -8717050996527626286L;
    private final String unitName;
    private final PersistenceContextType type;
    private final boolean multiThreaded;

    /**
     * Constructor.
     *
     * @param name            Name of the resource.
     * @param unitName        Persistence unit name.
     * @param type            the PersistenceContextType
     * @param serviceContract the service contract for the persistence unit
     * @param multiThreaded   true if the resource is accessed from a multi-threaded implementation
     */
    public PersistenceContextResource(String name,
                                      String unitName,
                                      PersistenceContextType type,
                                      ServiceContract serviceContract,
                                      boolean multiThreaded) {
        super(name, serviceContract, true);
        this.unitName = unitName;
        this.type = type;
        this.multiThreaded = multiThreaded;
    }

    /**
     * Returns the persistence unit name.
     *
     * @return the persistence unit name.
     */
    public final String getUnitName() {
        return this.unitName;
    }

    /**
     * Returns the persistence context type.
     *
     * @return the persistence context type
     */
    public PersistenceContextType getType() {
        return type;
    }

    /**
     * Returns true if the EntityManager will be accessed from a mutli-thread implementation.
     *
     * @return true if the EntityManager will be accessed from a mutli-thread implementation
     */
    public boolean isMultiThreaded() {
        return multiThreaded;
    }
}
