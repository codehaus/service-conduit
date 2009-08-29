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
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.resource.jndi.proxy;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

/**
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class AbstractProxy<D> {
    
    private static final String COMP_ENV = "java:comp/env/";
    
    private D delegate;
    private String jndiName;
    private String providerUrl;
    private String initialContextFactory;
    private boolean env;
    
    /**
     * Gets a reference to the delegate that is proxied.
     * 
     * @return Delegate that is proxied.
     */
    protected D getDelegate() {
        return delegate;
    }
    
    @Init
    @SuppressWarnings("unchecked")
    public void init() throws NamingException {
        
        Context ctx = null;
        
        try {
            
            Properties prop = new Properties();
            if(providerUrl != null) {
                prop.put(Context.PROVIDER_URL, providerUrl);
            }
            if(initialContextFactory != null) {
                prop.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
            }
            
            if(env) {
                jndiName = COMP_ENV + jndiName;
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
     * @param jndiName the jndiName to set
     */
    @Property(required = true)
    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    /**
     * @param providerUrl the providerUrl to set
     */
    @Property
    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    /**
     * @param initialContextFactory the initialContextFactory to set
     */
    @Property
    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    /**
     * @param env the env to set
     */
    @Property
    public void setEnv(boolean env) {
        this.env = env;
    }
    

}
