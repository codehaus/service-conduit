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
package org.sca4j.fabric.implementation.singleton;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.Wire;

/**
 * A SourceWireAttacher used to reinject singleton components after the runtime bootsrap.
 *
 * @version $Rev: 4790 $ $Date: 2008-06-08 08:14:42 -0700 (Sun, 08 Jun 2008) $
 */
@EagerInit
public class SingletonSourceWireAttacher implements SourceWireAttacher<SingletonWireSourceDefinition> {
    private final ComponentManager manager;

    public SingletonSourceWireAttacher(@Reference ComponentManager manager) {
        this.manager = manager;
    }

    public void attachToSource(SingletonWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

    public void detachFromSource(SingletonWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

    public void attachObjectFactory(SingletonWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target)
            throws WiringException {
        URI sourceId = UriHelper.getDefragmentedName(source.getUri());
        SingletonComponent<?> sourceComponent = (SingletonComponent<?>) manager.getComponent(sourceId);
        InjectableAttribute attribute = source.getValueSource();
        // Add the object factory for the target to be reinjected.
        // The InjectableAttribute identifies the injection site (a field or method) on the singleton instance.
        sourceComponent.addObjectFactory(attribute, objectFactory);
    }
}
