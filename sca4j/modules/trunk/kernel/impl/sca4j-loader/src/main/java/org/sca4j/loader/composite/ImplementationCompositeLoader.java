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
package org.sca4j.loader.composite;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.ElementLoadFailure;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.ResourceElement;

/**
 * Loader that handles an &lt;implementation.composite&gt; element.
 *
 * @version $Rev: 5196 $ $Date: 2008-08-11 20:22:10 +0100 (Mon, 11 Aug 2008) $
 */
@EagerInit
public class ImplementationCompositeLoader implements TypeLoader<CompositeImplementation> {
    private static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();

    static {
        ATTRIBUTES.put("name", "name");
        ATTRIBUTES.put("scdlLocation", "scdlLocation");
        ATTRIBUTES.put("scdlResource", "scdlResource");
        ATTRIBUTES.put("requires", "requires");
    }

    private final Loader loader;
    private final LoaderRegistry registry;
    private final MetaDataStore store;

    public ImplementationCompositeLoader(@Reference LoaderRegistry loader, @Reference(required = false)MetaDataStore store) {
        this.loader = loader;
        this.registry = loader;
        this.store = store;
    }

    @Init
    public void init() {
        registry.registerLoader(CompositeImplementation.IMPLEMENTATION_COMPOSITE, this);
    }

    @Destroy
    public void destroy() {
        registry.unregisterLoader(CompositeImplementation.IMPLEMENTATION_COMPOSITE);
    }

    public CompositeImplementation load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        assert CompositeImplementation.IMPLEMENTATION_COMPOSITE.equals(reader.getName());
        validateAttributes(reader, introspectionContext);
        // read name now b/c the reader skips ahead
        String nameAttr = reader.getAttributeValue(null, "name");
        String scdlLocation = reader.getAttributeValue(null, "scdlLocation");
        String scdlResource = reader.getAttributeValue(null, "scdlResource");
        LoaderUtil.skipToEndElement(reader);

        ClassLoader cl = introspectionContext.getTargetClassLoader();
        CompositeImplementation impl = new CompositeImplementation();
        URI contributionUri = introspectionContext.getContributionUri();
        URL url;
        if (scdlLocation != null) {
            try {
                url = new URL(introspectionContext.getSourceBase(), scdlLocation);
            } catch (MalformedURLException e) {
                MissingComposite failure = new MissingComposite("Composite file not found: " + scdlLocation, scdlLocation, reader);
                introspectionContext.addError(failure);
                return impl;
            }
            IntrospectionContext childContext = new DefaultIntrospectionContext(cl, contributionUri, url);
            Composite composite;
            try {
                composite = loader.load(url, Composite.class, childContext);
                if (childContext.hasErrors()) {
                    introspectionContext.addErrors(childContext.getErrors());
                }
                if (childContext.hasWarnings()) {
                    introspectionContext.addWarnings(childContext.getWarnings());
                }
                if (composite == null) {
                    // error loading composite, return
                    return null;
                }

            } catch (LoaderException e) {
                ElementLoadFailure failure = new ElementLoadFailure("Error loading element", e, reader);
                introspectionContext.addError(failure);
                return null;
            }
            impl.setName(composite.getName());
            impl.setComponentType(composite);

            return impl;
        } else if (scdlResource != null) {
            url = cl.getResource(scdlResource);
            if (url == null) {
                MissingComposite failure = new MissingComposite("Composite file not found: " + scdlResource, scdlResource, reader);
                introspectionContext.addError(failure);
                return impl;
            }
            IntrospectionContext childContext = new DefaultIntrospectionContext(cl, contributionUri, url);
            Composite composite;
            try {
                composite = loader.load(url, Composite.class, childContext);
                if (childContext.hasErrors()) {
                    introspectionContext.addErrors(childContext.getErrors());
                }
                if (childContext.hasWarnings()) {
                    introspectionContext.addWarnings(childContext.getWarnings());
                }
                if (composite == null) {
                    // error loading composite, return
                    return null;
                }
            } catch (LoaderException e) {
                ElementLoadFailure failure = new ElementLoadFailure("Error loading element", e, reader);
                introspectionContext.addError(failure);
                return null;
            }
            impl.setName(composite.getName());
            impl.setComponentType(composite);
            return impl;
        } else {
            if (store == null) {
                // throw error as this is invalid in a bootstrap environment
                throw new UnsupportedOperationException("scdlLocation or scdlResource must be supplied as no MetaDataStore is available");
            }
            if (nameAttr == null || nameAttr.length() == 0) {
                MissingAttribute failure = new MissingAttribute("Missing name attribute", "name", reader);
                introspectionContext.addError(failure);
                return null;
            }
            QName name = LoaderUtil.getQName(nameAttr, introspectionContext.getTargetNamespace(), reader.getNamespaceContext());

            try {
                ResourceElement<QName, Composite> element = store.resolve(contributionUri, Composite.class, name, introspectionContext);
                if (element == null) {
                    String id = name.toString();
                    MissingComposite failure = new MissingComposite("Composite with qualified name not found: " + id, id, reader);
                    introspectionContext.addError(failure);
                    return impl;
                }
                impl.setComponentType(element.getValue());
                return impl;
            } catch (MetaDataStoreException e) {
                ElementLoadFailure failure = new ElementLoadFailure("Error loading element", e, reader);
                introspectionContext.addError(failure);
                return null;

            }
        }

    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!ATTRIBUTES.containsKey(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
