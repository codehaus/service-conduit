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
package org.sca4j.xapool;

import java.sql.SQLException;

import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.spi.resource.DataSourceRegistry;

/**
 * Used for creating XA pool datasources.
 * 
 * @author meerajk
 * 
 */
@EagerInit
public class XaPoolFactory {

    @Reference public DataSourceRegistry dataSourceRegistry;
    @Reference public TransactionManager transactionManager;
    @Property(required=false) public DataSourceConfigCollection dataSourceConfigCollection;
    
    @Init public void start() throws SQLException {
        
        if (dataSourceConfigCollection == null) return;
        
        for (DataSourceConfig dataSourceConfig : dataSourceConfigCollection.dataSources) {
            
            XaPoolDataSource xaPoolDataSource = new XaPoolDataSource();
            xaPoolDataSource.driver = dataSourceConfig.driver;
            xaPoolDataSource.url = dataSourceConfig.url;
            xaPoolDataSource.minSize = dataSourceConfig.minSize;
            xaPoolDataSource.maxSize = dataSourceConfig.maxSize;
            xaPoolDataSource.user = dataSourceConfig.user;
            xaPoolDataSource.password = dataSourceConfig.password;
            xaPoolDataSource.dataSourceKeys = dataSourceConfig.keys.split(" ");
            
            xaPoolDataSource.transactionManager = transactionManager;
            xaPoolDataSource.dataSourceRegistry = dataSourceRegistry;
            
            xaPoolDataSource.start();
            
        }
    }
    

}
