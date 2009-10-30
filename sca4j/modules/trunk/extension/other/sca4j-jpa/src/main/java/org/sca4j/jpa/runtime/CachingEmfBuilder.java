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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.jpa.runtime;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.jpa.spi.delegate.EmfBuilderDelegate;

/**
 * Creates entity manager factories using the JPA provider SPI. Creation of entity manager factories are expensive operations and hence created
 * instances are cached.
 *
 * @version $Revision$ $Date$
 */
@Service(interfaces = {EmfBuilder.class, EmfCache.class})
public class CachingEmfBuilder implements EmfBuilder, EmfCache {

    private Map<String, EntityManagerFactory> cache = new HashMap<String, EntityManagerFactory>();
    private PersistenceUnitScanner scanner;
    private Map<String, EmfBuilderDelegate> delegates = new HashMap<String, EmfBuilderDelegate>();

    /**
     * Injects the scanner.
     *
     * @param scanner Injected scanner.
     */
    public CachingEmfBuilder(@Reference PersistenceUnitScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Injects the delegates.
     *
     * @param delegates Provider specific delegates.
     */
    @Reference(required = false)
    public void setDelegates(Map<String, EmfBuilderDelegate> delegates) {
        this.delegates = delegates;
    }

    public synchronized EntityManagerFactory build(String unitName, ClassLoader classLoader) {

        if (cache.containsKey(unitName)) {
            return cache.get(unitName);
        }

        EntityManagerFactory emf = createEntityManagerFactory(unitName, classLoader);
        cache.put(unitName, emf);

        return emf;

    }

    /**
     * Closes the entity manager factories.
     */
    @Destroy
    public void destroy() {
        for (EntityManagerFactory emf : cache.values()) {
            if (emf != null) {
                emf.close();
            }
        }
    }

    public EntityManagerFactory getEmf(String unitName) {
        return cache.get(unitName);
    }

    /*
    * Creates the entity manager factory using the JPA provider API.
    */
    private EntityManagerFactory createEntityManagerFactory(String unitName, ClassLoader classLoader) {

    	PersistenceUnitInfoImpl info = (PersistenceUnitInfoImpl) scanner.getPersistenceUnitInfo(unitName, classLoader);
        String providerClass = info.getPersistenceProviderClassName();
        String dataSourceName = info.getDataSourceName();

        EmfBuilderDelegate delegate = delegates.get(providerClass);
        return delegate.build(info, classLoader, dataSourceName);

    }
    
    /**
     * Closes all the entity manager factory on runtime exit.
     */
    @Destroy
    public void close() {
        for (EntityManagerFactory emf : this.cache.values()) {
            emf.close();
        }
    }

}
