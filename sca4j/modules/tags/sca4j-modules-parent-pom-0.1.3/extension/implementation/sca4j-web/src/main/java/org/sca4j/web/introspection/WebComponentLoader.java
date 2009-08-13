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
package org.sca4j.web.introspection;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.ElementLoadFailure;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.scdl.ComponentType;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;

/**
 * Loads <code><implementation.web></code> from a composite.
 *
 * @version $Rev: 3105 $ $Date: 2008-03-15 09:47:31 -0700 (Sat, 15 Mar 2008) $
 */
@EagerInit
public class WebComponentLoader implements TypeLoader<WebImplementation> {

    private LoaderRegistry registry;
    private WebImplementationIntrospector introspector;

    public WebComponentLoader(@Reference LoaderRegistry registry, @Reference WebImplementationIntrospector introspector) {
        this.registry = registry;
        this.introspector = introspector;
    }

    @Init
    public void init() {
        registry.registerLoader(WebImplementation.IMPLEMENTATION_WEB, this);
        registry.registerLoader(WebImplementation.IMPLEMENTATION_WEBAPP, this);
    }

    @Destroy
    public void destroy() {
        registry.unregisterLoader(WebImplementation.IMPLEMENTATION_WEB);
        registry.unregisterLoader(WebImplementation.IMPLEMENTATION_WEBAPP);
    }

    public WebImplementation load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {

        WebImplementation impl = new WebImplementation();
        introspector.introspect(impl, introspectionContext);

        try {
            ComponentType type = impl.getComponentType();
            // FIXME we should allow implementation to specify the component type;
            ComponentType componentType = loadComponentType(introspectionContext);
            for (Map.Entry<String, ReferenceDefinition> entry : componentType.getReferences().entrySet()) {
                type.add(entry.getValue());
            }
            for (Map.Entry<String, Property> entry : componentType.getProperties().entrySet()) {
                type.add(entry.getValue());
            }
        } catch (LoaderException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                // ignore since we allow component types not to be specified in the web app 
            } else {
                ElementLoadFailure failure = new ElementLoadFailure("Error loading web.componentType", e, reader);
                introspectionContext.addError(failure);
                return null;
            }
        }
        LoaderUtil.skipToEndElement(reader);
        return impl;
    }

    private ComponentType loadComponentType(IntrospectionContext context) throws LoaderException {
        URL url;
        try {
            url = new URL(context.getSourceBase(), "web.componentType");
        } catch (MalformedURLException e) {
            // this should not happen
            throw new LoaderException(e.getMessage(), e);
        }
        IntrospectionContext childContext = new DefaultIntrospectionContext(context.getTargetClassLoader(), null, url);
        ComponentType componentType = registry.load(url, ComponentType.class, childContext);
        componentType.setScope("COMPOSITE");
        if (childContext.hasErrors()) {
            context.addErrors(childContext.getErrors());
        }
        if (childContext.hasWarnings()) {
            context.addWarnings(childContext.getWarnings());
        }
        return componentType;
    }
}
