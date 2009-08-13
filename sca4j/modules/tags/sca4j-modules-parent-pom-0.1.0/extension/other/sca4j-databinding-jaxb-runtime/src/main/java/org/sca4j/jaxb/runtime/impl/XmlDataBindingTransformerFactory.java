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

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.host.Namespaces;
import org.sca4j.jaxb.runtime.spi.DataBindingTransformerFactory;
import org.sca4j.transform.PullTransformer;

/**
 * DataBindingTransformerFactory for converting between JAXB and XML string representations.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class XmlDataBindingTransformerFactory implements DataBindingTransformerFactory<String> {
    private static final QName DATATYPE_XML = new QName(Namespaces.SCA4J_NS, "dataType.xml");

    public QName getDataType() {
        return DATATYPE_XML;
    }

    public PullTransformer<String, Object> createToJAXBTransformer(JAXBContext context) {
        return new Xml2JAXBTransformer(context);
    }

    public PullTransformer<Object, String> createFromJAXBTransformer(JAXBContext context) {
        return new JAXB2XmlTransformer(context);
    }
}
