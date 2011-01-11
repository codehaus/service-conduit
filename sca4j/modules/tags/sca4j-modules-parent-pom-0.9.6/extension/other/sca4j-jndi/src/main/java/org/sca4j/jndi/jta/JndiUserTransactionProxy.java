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
package org.sca4j.jndi.jta;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.jndi.AbstractJndiProxy;
import org.sca4j.jndi.config.JndiEnvPrefix;
import org.sca4j.jndi.config.JndiUserTransactionConfig;
import org.sca4j.spi.resource.ResourceRegistry;

import javax.naming.NamingException;
import javax.transaction.*;
import java.sql.SQLException;

/**
 * Proxy class for a JNDI-based user transaction manager.
 *
 * @author deanb
 */
@EagerInit
public class JndiUserTransactionProxy extends AbstractJndiProxy<UserTransaction> implements UserTransaction {

    @Property(required=false) public JndiUserTransactionConfig userTransactionConfig;
    @Reference
    public ResourceRegistry resourceRegistry;

    @Init
    public void init() throws SQLException, NamingException {

        if (userTransactionConfig != null) {

            setEnvPrefix(JndiEnvPrefix.COMP);

            lookup(userTransactionConfig);

            resourceRegistry.registerResource(UserTransaction.class, "userTransaction", getDelegate());

        }

    }

    public void begin() throws NotSupportedException, SystemException {
        getDelegate().begin();
    }

    public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
        getDelegate().commit();
    }

    public int getStatus() throws SystemException {
        return getDelegate().getStatus();
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        getDelegate().rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        getDelegate().setRollbackOnly();
    }

    public void setTransactionTimeout(int timeout) throws SystemException {
        getDelegate().setTransactionTimeout(timeout);
    }

}