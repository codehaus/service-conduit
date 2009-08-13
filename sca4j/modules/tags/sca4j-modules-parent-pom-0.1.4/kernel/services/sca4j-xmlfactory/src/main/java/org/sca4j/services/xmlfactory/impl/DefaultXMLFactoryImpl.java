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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;

/**
 * Implementation of XMLFactory that uses the default factories provided by the StAX API.
 * <p/>
 * In general, this should only be used when it is known that the API and it's default implementation are adequate. For example, this could be used in
 * a Java6 environment when the implementation desired is the one from the JRE.
 *
 * @version $Rev: 3075 $ $Date: 2008-03-12 15:10:36 +0000 (Wed, 12 Mar 2008) $
 */
public class DefaultXMLFactoryImpl implements XMLFactory {
    public XMLInputFactory newInputFactoryInstance() throws XMLFactoryInstantiationException {
        return XMLInputFactory.newInstance();
    }

    public XMLOutputFactory newOutputFactoryInstance() throws XMLFactoryInstantiationException {
        return XMLOutputFactory.newInstance();
    }
}
