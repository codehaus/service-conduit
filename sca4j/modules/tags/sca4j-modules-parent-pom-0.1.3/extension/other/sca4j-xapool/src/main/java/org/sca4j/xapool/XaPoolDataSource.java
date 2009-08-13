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
package org.sca4j.xapool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.enhydra.jdbc.standard.StandardXADataSource;
import org.sca4j.spi.resource.DataSourceRegistry;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class XaPoolDataSource implements DataSource {

    private String user;
    private String password;
    private String url;
    private String driver;
    private List<String> dataSourceKeys;
    private int minSize = 10;
    private int maxSize = 10;

    private StandardXADataSource delegate;
    private TransactionManager transactionManager;
    private DataSourceRegistry dataSourceRegistry;
    private Map<Transaction, TransactedConnection> connectionCache = new ConcurrentHashMap<Transaction, TransactedConnection>();

    public Connection getConnection() throws SQLException {
		
        try {
            
            final Transaction transaction = transactionManager.getTransaction();
            
            if (transaction == null) {
                return delegate.getConnection();
            }
            
            TransactedConnection connection = connectionCache.get(transaction);
            if (connection == null) {
                final XAConnection xaConnection = delegate.getXAConnection();
                connection = new TransactedConnection(xaConnection);
                connectionCache.put(transaction, connection);
                transaction.registerSynchronization(new Synchronization() {
                    public void afterCompletion(int status) {
                        TransactedConnection connection = connectionCache.get(transaction);
                        connection.closeForReal();
                        connectionCache.remove(transaction);
                    }
                    public void beforeCompletion() {
                    }
                });
            }
            
            return connection;
            
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

    @Property(required = true)
    public void setDataSourceKeys(List<String> dataSourceKeys) {
        this.dataSourceKeys = dataSourceKeys;
    }

    @Property
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Property
    public void setUser(String user) {
        this.user = user;
    }

    @Property
    public void setPassword(String password) {
        this.password = password;
    }

    @Property
    public void setUrl(String url) {
        this.url = url;
    }

    @Property
    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Property
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    @Property
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Reference
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Reference
    public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }

    @Init
    public void start() throws SQLException {

        delegate = new StandardXADataSource();
        delegate.setTransactionManager(transactionManager);
        delegate.setUrl(url);
        delegate.setDriverName(driver);
        delegate.setPassword(password);
        delegate.setUser(user);
        delegate.setMinCon(minSize);
        delegate.setMaxCon(maxSize);

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
