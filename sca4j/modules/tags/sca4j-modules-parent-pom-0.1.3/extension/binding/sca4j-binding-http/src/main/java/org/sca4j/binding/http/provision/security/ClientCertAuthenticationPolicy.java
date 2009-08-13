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
package org.sca4j.binding.http.provision.security;

/**
 * Client cert authentication policy.
 *
 */
public class ClientCertAuthenticationPolicy implements AuthenticationPolicy {
    
    private final String trustStore;
    private final String keyStore;
    private final String trustStorePassword;
    private final String keyStorePassword;
    private final boolean classpath;
    
    /**
     * @param trustStore
     * @param keyStore
     * @param trustStorePassword
     * @param keyStorePassword
     * @param classpath
     */
    public ClientCertAuthenticationPolicy(String trustStore, String keyStore, String trustStorePassword, String keyStorePassword, boolean classpath) {
        this.trustStore = trustStore;
        this.keyStore = keyStore;
        this.trustStorePassword = trustStorePassword;
        this.keyStorePassword = keyStorePassword;
        this.classpath = classpath;
    }
    
    public boolean isClasspath() {
        return classpath;
    }

    /**
     * @return
     */
    public String getTrustStore() {
        return trustStore;
    }

    /**
     * @return
     */
    public String getKeyStore() {
        return keyStore;
    }

    /**
     * @return
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * @return
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

}
