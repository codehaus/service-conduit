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
