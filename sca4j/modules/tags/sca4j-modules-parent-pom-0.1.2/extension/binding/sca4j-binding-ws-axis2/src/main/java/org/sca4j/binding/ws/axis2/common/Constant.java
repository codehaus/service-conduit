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
package org.sca4j.binding.ws.axis2.common;

import javax.xml.namespace.QName;

/**
 * 
 * @version $Revision$ $Date$
 */
public interface Constant {
    public static final QName AXIS2_JAXWS_QNAME = new QName("urn:org.sca4j:binding:axis2", "jaxws");
    public static final String SOAP_ACTION = "soapAction";
    public static final String VALUE_TRUE = "true";
    
    public static final String CONFIG_ENABLE_MTOM = "enableMTOM";    
}
