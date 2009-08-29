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
package org.sca4j.jaxb.runtime.impl;

import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Reference;

import org.sca4j.jaxb.provision.ReferenceTransformingInterceptorDefinition;
import org.sca4j.jaxb.runtime.spi.DataBindingTransformerFactory;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.transform.PullTransformer;

/**
 * Builds a transforming interceptor for the reference side of a wire.
 *
 * @version $Revision$ $Date$
 */
public class ReferenceTransformingInterceptorBuilder
        extends AbstractTransformingInterceptorBuilder<ReferenceTransformingInterceptorDefinition, TransformingInterceptor<?, ?>> {
    private Map<QName, DataBindingTransformerFactory<?>> factories;

    public ReferenceTransformingInterceptorBuilder(@Reference ClassLoaderRegistry classLoaderRegistry) {
        super(classLoaderRegistry);
    }

    @Reference
    public void setFactories(Map<QName, DataBindingTransformerFactory<?>> factories) {
        this.factories = factories;
    }

    @SuppressWarnings({"unchecked"})
    protected TransformingInterceptor<?, ?> build(QName dataType, JAXBContext context, ClassLoader classLoader) throws TransformingBuilderException {
        DataBindingTransformerFactory<?> factory = factories.get(dataType);
        if (factory == null) {
            throw new TransformingBuilderException("No DataBindingTransformerFactory found for: " + dataType);
        }
        PullTransformer<Object, ?> inTransformer = factory.createFromJAXBTransformer(context);
        PullTransformer<?, Object> outTransformer = factory.createToJAXBTransformer(context);
        return new TransformingInterceptor(inTransformer, outTransformer, classLoader);
    }

}
