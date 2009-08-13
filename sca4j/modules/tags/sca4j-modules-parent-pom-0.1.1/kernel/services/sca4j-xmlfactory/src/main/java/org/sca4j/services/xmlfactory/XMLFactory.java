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

package org.sca4j.services.xmlfactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * This service has been added as a work around to a problem in JDK stax parser api
 * This allows to get instances of XML input and output factories
 */

public interface XMLFactory {

    /**
     * Return the runtime's XMLInputFactory implementation.
     *
     * @return the factory
     * @throws XMLFactoryInstantiationException if an error occurs loading the factory
     */
    XMLInputFactory newInputFactoryInstance() throws XMLFactoryInstantiationException;

    /**
     * Return the runtime's XMLOutputFactory implementation.
     *
     * @return the factory
     * @throws XMLFactoryInstantiationException if an error occurs loading the factory
     */
    XMLOutputFactory newOutputFactoryInstance() throws XMLFactoryInstantiationException;

}
