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
package org.sca4j.binding.ws.axis2.introspection;

import java.lang.reflect.Method;

import javax.jws.WebMethod;

import org.sca4j.binding.ws.axis2.common.Constant;
import org.sca4j.introspection.contract.OperationIntrospector;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ValidationContext;

/**
 * Introspects operations for the presence of JAX-WS annotations. JAX-WS annotations are used to configure the Axis2 engine.
 * 
 * @version $Revision$ $Date$
 */
public class JAXWSTypeIntrospector implements OperationIntrospector {

    public <T> void introspect(Operation<T> operation, Method method, ValidationContext context) {
        WebMethod webMethod = method.getAnnotation(WebMethod.class);
        if (webMethod != null) {
            String soapAction = webMethod.action();
            if (soapAction != null) {
                operation.addInfo(Constant.AXIS2_JAXWS_QNAME, Constant.SOAP_ACTION, soapAction);
            }
        }

    }
}
