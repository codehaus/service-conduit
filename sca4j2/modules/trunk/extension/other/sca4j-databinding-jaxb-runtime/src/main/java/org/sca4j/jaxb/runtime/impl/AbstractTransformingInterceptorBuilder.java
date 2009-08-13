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

import java.net.URI;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.sca4j.jaxb.provision.AbstractTransformingInterceptorDefinition;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.interceptor.InterceptorBuilder;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.Interceptor;

/**
 * Base implementation for JAXB transformers.
 *
 * @version $Revision$ $Date$
 */
public abstract class AbstractTransformingInterceptorBuilder<T extends AbstractTransformingInterceptorDefinition, I extends Interceptor>
        implements InterceptorBuilder<T, I> {
    private ClassLoaderRegistry classLoaderRegistry;

    public AbstractTransformingInterceptorBuilder(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public I build(T definition) throws BuilderException {
        URI classLoaderId = definition.getClassLoaderId();
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(classLoaderId);
        QName dataType = definition.getDataType();
        assert classLoader != null;
        try {
            Set<String> classNames = definition.getClassNames();
            JAXBContext context = getJAXBContext(classLoader, classNames);
            return build(dataType, context, classLoader);
        } catch (ClassNotFoundException e) {
            throw new TransformingBuilderException(e);
        } catch (JAXBException e) {
            throw new TransformingBuilderException(e);
        }
    }

    /**
     * Subtypes are responsible for creating a transforming interceptor.
     *
     * @param dataType    the datatype to transform to and from
     * @param context     the JAXB context to perform transformations with
     * @param classLoader the classloader for transformed types
     * @return the transforming interceptor
     * @throws TransformingBuilderException if an error occurs during the build process
     */
    protected abstract I build(QName dataType, JAXBContext context, ClassLoader classLoader) throws TransformingBuilderException;

    /**
     * Constructs a JAXB context by introspecting a set of classnames
     *
     * @param classLoader the classloader the context classes are to be loaded in
     * @param classNames  the context class names
     * @return a JAXB context
     * @throws JAXBException          if an error occurs creating the JAXB context
     * @throws ClassNotFoundException if a context class is not found
     */
    private JAXBContext getJAXBContext(ClassLoader classLoader, Set<String> classNames) throws JAXBException, ClassNotFoundException {
        Class<?>[] classes = new Class<?>[classNames.size()];
        int i = 0;
        for (String className : classNames) {
            classes[i++] = classLoaderRegistry.loadClass(classLoader, className);
        }
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            return JAXBContext.newInstance(classes);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }

}
