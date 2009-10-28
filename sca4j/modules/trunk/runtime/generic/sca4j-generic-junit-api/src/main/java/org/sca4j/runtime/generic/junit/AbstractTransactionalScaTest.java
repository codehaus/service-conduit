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
package org.sca4j.runtime.generic.junit;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.sca4j.host.jpa.EmfBuilder;

/**
 * Unit test with transaction capabilities.
 *
 */
public abstract class AbstractTransactionalScaTest extends AbstractScaTest {
    
    private TransactionManager transactionManager;
    private boolean commit;

    /**
     * @param applicationScdl Application SCDL to test.
     * @param commit True to commit transaction.
     */
    public AbstractTransactionalScaTest(String applicationScdl, boolean commit) {
        super(applicationScdl);
        this.commit = commit;
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        transactionManager = genericRuntime.getSystemComponent(TransactionManager.class, URI.create("sca4j://runtime/TransactionManager"));
        transactionManager.begin();
        setUpInternal();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        tearDownInternal();
        if (commit) {
            transactionManager.commit();
        } else {
            transactionManager.rollback();
        }
    }
    
    /**
     * Gets a names entity manager.
     * @param unitName Persitence unit name.
     * @return ENtity manager.
     */
    protected EntityManager getEntityManager(String unitName) {
        EmfBuilder emfBuilder = genericRuntime.getSystemComponent(EmfBuilder.class, URI.create("sca4j://runtime/CachingEmfBuilder"));
        return emfBuilder.build(unitName, getClass().getClassLoader()).createEntityManager();
    }
    
    /**
     * Called by sub-classes.
     */
    protected abstract void setUpInternal();
    
    /**
     * Called by sub-classes.
     */
    protected abstract void tearDownInternal();

}
