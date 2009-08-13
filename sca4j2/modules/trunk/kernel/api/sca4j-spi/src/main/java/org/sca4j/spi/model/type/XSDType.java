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
package org.sca4j.spi.model.type;

import java.lang.reflect.Type;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.sca4j.scdl.DataType;

/**
 * Specialization of DataType representing types from the XML Schema type system.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public abstract class XSDType extends DataType<QName> {
    private static final long serialVersionUID = 4837060732513291971L;
    public static final String XSD_NS = XMLConstants.XML_NS_URI;

    protected XSDType(Type physical, QName logical) {
        super(physical, logical);
    }
}
