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
package org.sca4j.binding.ws.control;

/**
 * @version $Rev: 3145 $ $Date: 2008-03-19 11:04:25 +0000 (Wed, 19 Mar 2008) $
 */
public class MissingWebServiceExtensionException extends WsBindingGenerationException {
    public MissingWebServiceExtensionException() {
        super("A web services stack extension must be instaled", null);
    }
}
