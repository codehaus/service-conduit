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
package org.sca4j.binding.ws.axis2.provision;

import java.util.Set;

import org.sca4j.binding.ws.axis2.provision.AxisPolicy;

/**
 * @version $Revision$ $Date$
 * 
 * TODO Support overloading.
 */
public interface Axis2PolicyAware {

    /**
     * Gets all the policies for requested operation.
     * 
     * @param operation Operation against which policies are requested.
     * @return Policies defined against the operation.
     */
    public Set<AxisPolicy> getPolicies(String operation);

    /**
     * Adds a policy against an operation.
     * 
     * @param operation Operation against which policy is added.
     * @param axisPolicy Policy that needs to be added.
     */
    public void addPolicy(String operation, AxisPolicy axisPolicy);

}
