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
package org.sca4j.scdl;

/**
 * Represents a callback definition.
 *
 * @version $Rev: 1450 $ $Date: 2007-10-04 12:03:56 -0700 (Thu, 04 Oct 2007) $
 */
public class CallbackDefinition extends ModelObject {
    private static final long serialVersionUID = -1845071329121684755L;

    private String name;
    private ServiceContract<?> serviceContract;

    public CallbackDefinition(String name, ServiceContract<?> serviceContract) {
        this.name = name;
        this.serviceContract = serviceContract;
    }

    /**
     * The name of the callback
     *
     * @return the name of the callback
     */
    public String getName() {
        return name;
    }

    /**
     * Returned the service contract for the callback.
     *
     * @return the service contract
     */
    public ServiceContract<?> getServiceContract() {
        return serviceContract;
    }

}
