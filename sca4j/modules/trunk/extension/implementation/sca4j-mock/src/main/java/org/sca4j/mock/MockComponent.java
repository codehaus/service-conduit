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
package org.sca4j.mock;

import java.net.URI;
import java.util.Map;

import org.sca4j.scdl.PropertyValue;
import org.sca4j.spi.AbstractLifecycle;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;
import org.oasisopen.sca.ComponentContext;

/**
 * @version $Revision$ $Date$
 */
public class MockComponent<T> extends AbstractLifecycle implements AtomicComponent<T> {
    
    private final URI componentId;
    private final ObjectFactory<T> objectFactory;
    
    public MockComponent(URI componentId, ObjectFactory<T> objectFactory) {
        this.componentId = componentId;
        this.objectFactory = objectFactory;
    }

    public URI getUri() {
        return componentId;
    }

    @SuppressWarnings("unchecked")
    public ObjectFactory<T> createObjectFactory() {
        return objectFactory;
    }

    public <R> ObjectFactory<R> createObjectFactory(Class<R> type, String serviceName) throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public InstanceWrapper<T> createInstanceWrapper(WorkContext workContext) throws ObjectCreationException {
        return null;
    }

    public URI getGroupId() {
        return null;
    }

    public int getInitLevel() {
        return 0;
    }

    public long getMaxAge() {
        return 0;
    }

    public long getMaxIdleTime() {
        return 0;
    }

    public boolean isEagerInit() {
        return false;
    }

    public ComponentContext getComponentContext() {
        return null;
    }

    public Map<String, PropertyValue> getDefaultPropertyValues() {
        return null;
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue> propertyValues) {
    }

}
