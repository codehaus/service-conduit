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
package org.sca4j.spi.builder.component;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * Component that handles attachment of a wire at the source side of the invocation chain.
 * <p/>
 * Implementations attach physical wires to component implementations so that the implementation can invoke other components. These may be for
 * references or for callbacks.
 *
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
public interface SourceWireAttacher<PWSD extends PhysicalWireSourceDefinition> {
    /**
     * Attaches a wire to a source component or and incoming binding.
     *
     * @param source metadata for performing the attach
     * @param target metadata for performing the attach
     * @param wire   the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachToSource(PWSD source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException;


    /**
     * Detach a wire from a source component
     * @param source
     * @param target
     * @param wire
     */
    void detachFromSource(PWSD source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException;
    /**
     * Attaches an ObjectFactory to a source component.
     *
     * @param source        the definition of the component reference to attach to
     * @param objectFactory an ObjectFactory that can produce values compatible with the reference
     * @param target    the target definition for the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachObjectFactory(PWSD source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) throws WiringException;
}
