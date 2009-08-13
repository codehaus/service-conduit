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
package org.sca4j.binding.http.control.security;

import org.sca4j.binding.http.control.PolicyApplier;
import org.sca4j.binding.http.provision.PolicyAware;
import org.sca4j.binding.http.provision.security.AuthenticationPolicy;
import org.sca4j.binding.http.provision.security.ClientCertAuthenticationPolicy;
import org.w3c.dom.Element;

/**
 * @author meerajk
 *
 */
public class ClientCertAuthPolicyApplier implements PolicyApplier {

    /* (non-Javadoc)
     * @see org.sca4j.binding.http.control.PolicyApplier#applyPolicy(org.sca4j.binding.http.control.PolicyAware, org.w3c.dom.Element)
     */
    public void applyPolicy(PolicyAware policyAware, Element policyElement) {
        
        String trustStore = policyElement.getAttribute("trustStore");
        String keyStore = policyElement.getAttribute("keyStore");
        String trustStorePassword = policyElement.getAttribute("trustStorePassword");
        String keyStorePassword = policyElement.getAttribute("keyStorePassword");
        boolean classpath = Boolean.valueOf(policyElement.getAttribute("classpath"));
        
        AuthenticationPolicy policy = new ClientCertAuthenticationPolicy(trustStore, keyStore, trustStorePassword, keyStorePassword, classpath);
        policyAware.setAuthenticationPolicy(policy);
        
    }

}
