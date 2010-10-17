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
 */
package org.sca4j.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.sca4j.jndi.config.JndiEnvPrefix;
import org.sca4j.jndi.config.JndiResourceConfig;


/**
 * Abtract JNDI proxy class
 * 
 * @author deanb
 */
public class AbstractJndiProxy<D> {
    
    private D delegate;
    private JndiResourceConfig resourceConfig;
    private JndiEnvPrefix envPrefix;
    
    @SuppressWarnings("unchecked")
    public void lookup(JndiResourceConfig resourceConfig) throws NamingException {
        
        Context ctx = null;
        String jndiName = resourceConfig.jndiName;

        this.resourceConfig = resourceConfig;
        
        try {
            
            Properties prop = new Properties();
            if(resourceConfig.providerUrl != null) {
                prop.put(Context.PROVIDER_URL, resourceConfig.providerUrl);
            }
            if(resourceConfig.initialContextFactory != null) {
                prop.put(Context.INITIAL_CONTEXT_FACTORY, resourceConfig.initialContextFactory);
            }
            if(resourceConfig.securityProtocol != null) {
                prop.put(Context.SECURITY_PROTOCOL, resourceConfig.securityProtocol);
            }
            if(resourceConfig.securityAuthentication != null) {
                prop.put(Context.SECURITY_AUTHENTICATION, resourceConfig.securityAuthentication);
            }
            if(resourceConfig.securityPrincipal != null) {
                prop.put(Context.SECURITY_PRINCIPAL, resourceConfig.securityPrincipal);
            }
            if(resourceConfig.securityCredentials != null) {
                prop.put(Context.SECURITY_CREDENTIALS, resourceConfig.securityCredentials);
            }              
            
            if(resourceConfig.env) {
                 jndiName = envPrefix.value() + resourceConfig.jndiName;
            }

            ctx = new InitialContext(prop);
            
            delegate = (D) ctx.lookup(jndiName);
            
        } finally {
            if(ctx != null) {
                ctx.close();
            }
        }
        
    }

    /**
     * Gets a reference to the delegate that is proxied.
     *
     * @return Delegate that is proxied.
     */
    protected D getDelegate() {
        return delegate;
    }

    /**
     * @param envPrefix  the env prefix to set
     */
    public void setEnvPrefix(JndiEnvPrefix envPrefix) {
        this.envPrefix = envPrefix;
    }

    /**
     * Gets the resource config
     * @return config
     */
    public JndiResourceConfig getResourceConfig() {
        if (resourceConfig == null) {
            resourceConfig = new JndiResourceConfig();
        }
        return resourceConfig;
    }
    
}
