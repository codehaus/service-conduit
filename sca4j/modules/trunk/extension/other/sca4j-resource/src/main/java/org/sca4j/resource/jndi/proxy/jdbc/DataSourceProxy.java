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
package org.sca4j.resource.jndi.proxy.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.resource.jndi.proxy.AbstractProxy;
import org.sca4j.spi.resource.DataSourceRegistry;

/**
 * Proxy class for a JNDI-based datasource.
 * 
 * @version $Revision$ $Date$
 */
public class DataSourceProxy extends AbstractProxy<DataSource> implements DataSource {
    
    private DataSourceRegistry dataSourceRegistry;
    private List<String> dataSourceKeys;

    @Property
    public void setDataSourceKeys(List<String> dataSourceKeys) {
        this.dataSourceKeys = dataSourceKeys;
    }

    @Reference
    public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }
    
    @Override
    @Init
    public void init() throws NamingException {
        super.init();
        for (String dataSourceKey : dataSourceKeys) {
            dataSourceRegistry.registerDataSource(dataSourceKey, this);
        }
    }

    public Connection getConnection() throws SQLException {
        return getDelegate().getConnection();
    }

    public Connection getConnection(String userName, String password) throws SQLException {
        return getDelegate().getConnection(userName, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return getDelegate().getLogWriter();
    }

    public int getLoginTimeout() throws SQLException {
        return getDelegate().getLoginTimeout();
    }

    public void setLogWriter(PrintWriter writer) throws SQLException {
        getDelegate().setLogWriter(writer);
    }

    public void setLoginTimeout(int timeout) throws SQLException {
        getDelegate().setLoginTimeout(timeout);
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
