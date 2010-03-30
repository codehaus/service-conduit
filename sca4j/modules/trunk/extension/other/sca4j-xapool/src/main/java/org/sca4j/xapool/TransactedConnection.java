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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import javax.sql.XAConnection;

public class TransactedConnection implements Connection {
    
    private Connection wrappedConnection;
    private XAConnection xaConnection;
    
    public TransactedConnection(XAConnection xaConnection) throws SQLException {
        this.wrappedConnection = xaConnection.getConnection();
        this.xaConnection = xaConnection;
    }
    
    /**
     * Closes the pooled and XA connections.
     */
    public void closeForReal() {
        try {
            // wrappedConnection.close();
            xaConnection.close();
        } catch (SQLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Delegate operation.
     */
    public void clearWarnings() throws SQLException {
        wrappedConnection.clearWarnings();
    }

    /**
     * Don't close the physical connection.
     */
    public void close() throws SQLException {
    }

    /**
     * Delegate operation.
     */
    public void commit() throws SQLException {
        wrappedConnection.commit();
    }

    /**
     * Delegate operation.
     */
    public Statement createStatement() throws SQLException {
        return wrappedConnection.createStatement();
    }

    /**
     * Delegate operation.
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return wrappedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Delegate operation.
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * Delegate operation.
     */
    public boolean getAutoCommit() throws SQLException {
        return wrappedConnection.getAutoCommit();
    }

    /**
     * Delegate operation.
     */
    public String getCatalog() throws SQLException {
        return wrappedConnection.getCatalog();
    }

    /**
     * Delegate operation.
     */
    public int getHoldability() throws SQLException {
        return wrappedConnection.getHoldability();
    }

    /**
     * Delegate operation.
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return wrappedConnection.getMetaData();
    }

    /**
     * Delegate operation.
     */
    public int getTransactionIsolation() throws SQLException {
        return wrappedConnection.getTransactionIsolation();
    }

    /**
     * Delegate operation.
     */
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return wrappedConnection.getTypeMap();
    }

    /**
     * Delegate operation.
     */
    public SQLWarning getWarnings() throws SQLException {
        return wrappedConnection.getWarnings();
    }

    /**
     * Delegate operation.
     */
    public boolean isClosed() throws SQLException {
        return wrappedConnection.isClosed();
    }

    /**
     * Delegate operation.
     */
    public boolean isReadOnly() throws SQLException {
        return wrappedConnection.isReadOnly();
    }

    /**
     * Delegate operation.
     */
    public String nativeSQL(String sql) throws SQLException {
        return wrappedConnection.nativeSQL(sql);
    }

    /**
     * Delegate operation.
     */
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Delegate operation.
     */
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Delegate operation.
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        return wrappedConnection.prepareCall(sql);
    }

    /**
     * Delegate operation.
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Delegate operation.
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Delegate operation.
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * Delegate operation.
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return wrappedConnection.prepareStatement(sql, columnIndexes);
    }

    /**
     * Delegate operation.
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return wrappedConnection.prepareStatement(sql, columnNames);
    }

    /**
     * Delegate operation.
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return wrappedConnection.prepareStatement(sql);
    }

    /**
     * Delegate operation.
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        wrappedConnection.releaseSavepoint(savepoint);
    }

    /**
     * Delegate operation.
     */
    public void rollback() throws SQLException {
        wrappedConnection.rollback();
    }

    /**
     * Delegate operation.
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        wrappedConnection.rollback(savepoint);
    }

    /**
     * Delegate operation.
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        wrappedConnection.setAutoCommit(autoCommit);
    }

    /**
     * Delegate operation.
     */
    public void setCatalog(String catalog) throws SQLException {
        wrappedConnection.setCatalog(catalog);
    }

    /**
     * Delegate operation.
     */
    public void setHoldability(int holdability) throws SQLException {
        wrappedConnection.setHoldability(holdability);
    }

    /**
     * Delegate operation.
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        wrappedConnection.setReadOnly(readOnly);
    }

    /**
     * Delegate operation.
     */
    public Savepoint setSavepoint() throws SQLException {
        return wrappedConnection.setSavepoint();
    }

    /**
     * Delegate operation.
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        return wrappedConnection.setSavepoint(name);
    }

    /**
     * Delegate operation.
     */
    public void setTransactionIsolation(int level) throws SQLException {
        wrappedConnection.setTransactionIsolation(level);
    }

    /**
     * Delegate operation.
     */
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        wrappedConnection.setTypeMap(map);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return wrappedConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return wrappedConnection.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return wrappedConnection.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return wrappedConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return wrappedConnection.createSQLXML();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return wrappedConnection.createStruct(typeName, attributes);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return wrappedConnection.getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return wrappedConnection.getClientInfo(name);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return wrappedConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        wrappedConnection.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        wrappedConnection.setClientInfo(name, value);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return wrappedConnection.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return wrappedConnection.unwrap(iface);
    }

}
