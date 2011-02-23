/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 * 
 */
package org.sca4j.binding.sftp.common;

import static org.sca4j.host.Namespaces.SCA4J_NS;

import javax.xml.namespace.QName;

/**
 * SFTP authentication policy.
 * 
 * @author DhillonN
 */
public abstract class SecurityPolicy {
    public static final QName USERNAME_POLICY_QNAME = new QName(SCA4J_NS, "userNameSecurity");
    public static final QName PKI_POLICY_QNAME = new QName(SCA4J_NS, "pkiSecurity");
    private final String user;
    
    protected SecurityPolicy(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public static UsernamePasswordSecurity createUserPasswordAuthPolicy(String user, String password) {
        return new UsernamePasswordSecurity(user, password);
    }
    
    public static PkiSecurity createPkiAuthPolicy(String user, String privateKeyLocation, String passphrase) {
        return new PkiSecurity(user, privateKeyLocation, passphrase);
    }

    /**
     * Policy based on username-password authentication
     */
    public static class UsernamePasswordSecurity extends SecurityPolicy {
        private final String password;

        public UsernamePasswordSecurity(String user, String password) {
            super(user);
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * Policy based on PKI authentication.
     */
    public static class PkiSecurity extends SecurityPolicy {
        private final String identityFile;
        private String passphrase;
        
        public PkiSecurity(String user, String identityFile, String passphrase) {
            super(user);
            this.identityFile = identityFile;
            this.passphrase = passphrase;
        }

        public String getIdentityFile() {
            return identityFile;
        }
        public String getPassphrase() {
            return passphrase;
        }

        public void setPassphrase(String passphrase) {
            this.passphrase = passphrase;
        }
    }
}
