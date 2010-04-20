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
 */
package org.sca4j.bpel.runtime;

import java.net.URI;
import java.util.Map;

import org.sca4j.scdl.PropertyValue;
import org.sca4j.spi.AbstractLifecycle;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;

public class BpelComponent<T> extends AbstractLifecycle implements AtomicComponent<T> {
    
    private URI uri;
    private URI groupId;
    
    public BpelComponent(URI uri, URI groupId) {
        this.uri = uri;
        this.groupId = groupId;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public URI getGroupId() {
        return groupId;
    }

    @Override
    public InstanceWrapper<T> createInstanceWrapper(WorkContext workContext) throws ObjectCreationException {
        return null;
    }

    @Override
    public ObjectFactory<T> createObjectFactory() {
        return null;
    }

    @Override
    public <R> ObjectFactory<R> createObjectFactory(Class<R> type, String serviceName) throws ObjectCreationException {
        return null;
    }

    @Override
    public boolean isEagerInit() {
        return false;
    }

    @Override
    public Map<String, PropertyValue> getDefaultPropertyValues() {
        return null;
    }

    @Override
    public void setDefaultPropertyValues(Map<String, PropertyValue> defaultPropertyValues) {
    }

    @Override
    public int getInitLevel() {
        return 0;
    }

    @Override
    public long getMaxAge() {
        return 0;
    }

    @Override
    public long getMaxIdleTime() {
        return 0;
    }

}
