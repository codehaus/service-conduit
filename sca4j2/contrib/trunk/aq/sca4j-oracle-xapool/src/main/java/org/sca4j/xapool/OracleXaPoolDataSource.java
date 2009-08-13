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

/*
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
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
package org.sca4j.xapool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.transaction.TransactionManager;

import oracle.jdbc.xa.client.OracleXADataSource;

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
public class OracleXaPoolDataSource extends OracleXADataSource  {


    private static final long serialVersionUID = 1L;

    private String user;
    private String password;
    private String url;
    private String driver;
    private List<String> dataSourceKeys;

    private StandardXADataSource delegate;
    private TransactionManager transactionManager;
    private DataSourceRegistry dataSourceRegistry;

    /**
     * Constructor
     * @throws SQLException
     */
    public OracleXaPoolDataSource() throws SQLException {
        super();
    }

    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return delegate.getConnection(username, password);
    }

    public PrintWriter getLogWriter()  {
        return delegate.getLogWriter();
    }

    public int getLoginTimeout() {
        return delegate.getLoginTimeout();
    }

    public void setLogWriter(PrintWriter out)  {
        delegate.setLogWriter(out);
    }

    @Property(required = true)
    public void setDataSourceKeys(List<String> dataSourceKeys) {
        this.dataSourceKeys = dataSourceKeys;
    }

    @Property
    public void setLoginTimeout(int seconds)  {
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


        assignOnOracleDS();

        for (String dataSourceKey : dataSourceKeys) {
            dataSourceRegistry.registerDataSource(dataSourceKey, assignOnOracleDS());
        }

    }

    /*
     * For oracle
     */
    private OracleXADataSource assignOnOracleDS() {
        super.setURL(url);
        super.setUser(user);
        super.setPassword(password);
        return this;
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
