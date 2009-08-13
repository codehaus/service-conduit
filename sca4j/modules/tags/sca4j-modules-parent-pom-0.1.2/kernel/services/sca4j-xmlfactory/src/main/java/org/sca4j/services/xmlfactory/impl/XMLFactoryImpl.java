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

package org.sca4j.services.xmlfactory.impl;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Property;

import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.services.xmlfactory.XMLFactory;

/**
 * An implementation of XMLFactory that uses WoodStox stax parser for input &
 * output factories.
 * Alternately the runtime can be configured to use a different input and ouput factories
 * as properties in the scdl file
 */
public final class XMLFactoryImpl implements XMLFactory {

    private final String inputFactoryName;
    private final String outputFactoryName;
    private final ClassLoader classLoader = getClass().getClassLoader();

    public XMLFactoryImpl() {
        this("com.ctc.wstx.stax.WstxInputFactory", "com.ctc.wstx.stax.WstxOutputFactory");
    }

    @Constructor
    public XMLFactoryImpl(@Property(name = "input")String inputFactoryName,
                          @Property(name = "output")String outputFactoryName) {
        this.inputFactoryName = inputFactoryName;
        this.outputFactoryName = outputFactoryName;
    }

    /**
     * see XMLFactory
     * @return
     * @throws FactoryConfigurationError
     */
    public XMLInputFactory newInputFactoryInstance() throws FactoryConfigurationError {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return newInputFactoryInstance(inputFactoryName, classLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
    }

    /**
     * see XMLFactory
     * @return
     * @throws FactoryConfigurationError
     */
    public XMLOutputFactory newOutputFactoryInstance() throws FactoryConfigurationError {
        return newOutputFactoryInstance(outputFactoryName, classLoader);
    }

    private XMLInputFactory newInputFactoryInstance(String factoryName, ClassLoader cl)
            throws XMLFactoryInstantiationException {
        try {
            Class clazz = cl.loadClass(factoryName);
            return (XMLInputFactory) clazz.newInstance();
        } catch (InstantiationException ie) {
            throw new XMLFactoryInstantiationException("Error instantiating factory", factoryName, ie);
        } catch (IllegalAccessException iae) {
            throw new XMLFactoryInstantiationException("Error instantiating factory", factoryName, iae);
        } catch (ClassNotFoundException cnfe) {
            throw new XMLFactoryInstantiationException("Error loading factory", factoryName, cnfe);
        }
    }

    private XMLOutputFactory newOutputFactoryInstance(String factoryName, ClassLoader cl)
            throws FactoryConfigurationError {
        try {
            Class clazz = cl.loadClass(factoryName);
            return (XMLOutputFactory) clazz.newInstance();
        } catch (InstantiationException ie) {
            throw new XMLFactoryInstantiationException("Error instantiating factory", factoryName, ie);
        } catch (IllegalAccessException iae) {
            throw new XMLFactoryInstantiationException("Error instantiating factory", factoryName, iae);
        } catch (ClassNotFoundException cnfe) {
            throw new XMLFactoryInstantiationException("Error loading factory", factoryName, cnfe);
        }
    }

}
