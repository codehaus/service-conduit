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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

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

}
