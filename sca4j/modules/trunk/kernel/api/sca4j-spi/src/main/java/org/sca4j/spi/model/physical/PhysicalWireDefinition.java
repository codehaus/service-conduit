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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.spi.model.physical;

import java.util.HashSet;
import java.util.Set;

/**
 * Model class representing the portable definition of a wire.
 * <p/>
 * The definition describes a wire between a source component and a target component, defining how the wire should be attached at both ends. It also
 * describes the operations available on the wire, and whether the connection between the two components can be optimized.
 *
 * @version $Rev: 3715 $ $Date: 2008-04-24 18:54:02 +0100 (Thu, 24 Apr 2008) $
 */
public class PhysicalWireDefinition {

    // Source definition
    private PhysicalWireSourceDefinition source;

    // Target definition
    private PhysicalWireTargetDefinition target;

    // Collection of forward operations
    private final Set<PhysicalOperationDefinition> operations;

    private boolean optimizable;

    public PhysicalWireDefinition() {
        operations = new HashSet<PhysicalOperationDefinition>();
    }

    public PhysicalWireDefinition(PhysicalWireSourceDefinition source, PhysicalWireTargetDefinition target) {
        this.source = source;
        this.target = target;
        operations = new HashSet<PhysicalOperationDefinition>();
    }

    public PhysicalWireDefinition(PhysicalWireSourceDefinition source,
                                  PhysicalWireTargetDefinition target,
                                  Set<PhysicalOperationDefinition> operations) {
        this.source = source;
        this.target = target;
        this.operations = operations;
    }

    /**
     * Returns true if the wire can be optimized.
     *
     * @return true if the wire can be optimized
     */
    public boolean isOptimizable() {
        return optimizable;
    }

    /**
     * Sets whether the wire can be optimized.
     *
     * @param optimizable whether the wire can be optimized
     */
    public void setOptimizable(boolean optimizable) {
        this.optimizable = optimizable;
    }

    /**
     * Adds an operation definition.
     *
     * @param operation Operation to be added.
     */
    public void addOperation(PhysicalOperationDefinition operation) {
        operations.add(operation);
    }


    /**
     * Returns the available operations.
     *
     * @return Collection of operations.
     */
    public Set<PhysicalOperationDefinition> getOperations() {
        return operations;
    }

    /**
     * Returns the physical definition for the source side of the wire.
     *
     * @return the physical definition for the source side of the wire
     */
    public PhysicalWireSourceDefinition getSource() {
        return source;
    }

    /**
     * Sets the physical definition for the source side of the wire.
     *
     * @param source the physical definition for the source side of the wire
     */
    public void setSource(PhysicalWireSourceDefinition source) {
        this.source = source;
    }

    /**
     * Returns the physical definition for the target side of the wire.
     *
     * @return the physical definition for the target side of the wire
     */
    public PhysicalWireTargetDefinition getTarget() {
        return target;
    }

    /**
     * Sets the physical definition for the target side of the wire.
     *
     * @param target the physical definition for the target side of the wire
     */
    public void setTarget(PhysicalWireTargetDefinition target) {
        this.target = target;
    }

}
