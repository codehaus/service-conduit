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
package org.sca4j.xapool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.dbcp.BasicDataSource;
import org.osoa.sca.annotations.EagerInit;
import org.sca4j.host.SCA4JRuntimeException;
import org.sca4j.spi.resource.DataSourceRegistry;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class XaPoolDataSource implements DataSource {

    String user, password, url, driver;
    String[] dataSourceKeys;
    int minSize, maxSize;

    TransactionManager transactionManager;
    DataSourceRegistry dataSourceRegistry;

    private Map<Transaction, TransactedConnection> connectionCache = new ConcurrentHashMap<Transaction, TransactedConnection>();
    private BasicDataSource delegate;

    public Connection getConnection() throws SQLException {

        try {

            final Transaction transaction = transactionManager.getTransaction();

            if (transaction == null) {
                Connection connection = delegate.getConnection();
                connection.setAutoCommit(true);
                return connection;
            }

            TransactedConnection transactedConnection = connectionCache.get(transaction);
            if (transactedConnection == null) {
                Connection connection = delegate.getConnection();
                connection.setAutoCommit(false);
                transactedConnection = new TransactedConnection(connection);
                connectionCache.put(transaction, transactedConnection);
                transaction.registerSynchronization(new Synchronization() {
                    public void afterCompletion(int status) {
                        TransactedConnection transactedConnection = connectionCache.remove(transaction);
                        try {
                            if (status == Status.STATUS_COMMITTED) {
                                transactedConnection.commit();
                            } else {
                                transactedConnection.rollback();
                            }
                        } catch (SQLException e) {
                            throw new SCA4JRuntimeException(e) {
                            };
                        } finally {
                            transactedConnection.closeForReal();
                        }
                    }
                    public void beforeCompletion() {
                    }
                });
            }

            return transactedConnection;

        } catch (SystemException e) {
            throw new SQLException(e.getMessage());
        } catch (RollbackException e) {
            throw new SQLException(e.getMessage());
        }

    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    public void start() throws SQLException {

        delegate = new BasicDataSource();
        delegate.setUrl(url);
        delegate.setDriverClassName(driver);
        delegate.setPassword(password);
        delegate.setUsername(user);
        delegate.setMinIdle(minSize);
        delegate.setMaxActive(maxSize);

        for (String dataSourceKey : dataSourceKeys) {
            dataSourceRegistry.registerDataSource(dataSourceKey, this);
        }

    }

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		//return getDelegate().isWrapperFor(iface);
		throw new UnsupportedOperationException("isWrapperFor not supported");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		//return unwrap(iface);
		throw new UnsupportedOperationException("unwrap not supported");
	}


}
