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

package org.sca4j.idl.wsdl.processor;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaType;
import org.sca4j.scdl.Operation;

/**
 * Abstraction for processing WSDL.
 *
 * @version $Revison$ $Date: 2008-02-06 18:08:27 +0000 (Wed, 06 Feb 2008) $
 */
public interface WsdlProcessor {

    /**
     * Get the list of operations from a WSDL 1.1 port type or WSDL 2.0 interface.
     * 
     * @param portTypeOrInterfaceName Qualified name of the WSDL 1.1 port type or WSDL 2.0 interface.
     * @param wsdlUrl The URL to the WSDL.
     * @return List of operations.
     */
    List<Operation<XmlSchemaType>> getOperations(QName portTypeOrInterfaceName, URL wsdlUrl);

}
