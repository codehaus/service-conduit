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
package org.sca4j.atomikos.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.spi.resource.ResourceRegistry;

import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

/**
 * Used for creating XA pool datasources.
 * 
 * @author meerajk
 * 
 */
@EagerInit
public class DataSourceFactory {

    @Property(required=false) public DataSourceConfigCollection dataSourceConfigCollection;
    @Reference public ResourceRegistry resourceRegistry;
    private List<AbstractDataSourceBean> dataSourceBeans;
    
    @Init 
    public void start() throws SQLException {
        
        if (dataSourceConfigCollection == null) return;
        
        dataSourceBeans = new ArrayList<AbstractDataSourceBean>();        
        for (DataSourceConfig dataSourceConfig : dataSourceConfigCollection.dataSources) {

            boolean xa = XADataSource.class.isAssignableFrom(dataSourceConfig.driver);
            AbstractDataSourceBean abstractDataSourceBean = null;
            if (xa) {
                AtomikosDataSourceBean bean = new AtomikosDataSourceBean();
                bean.setXaDataSourceClassName(dataSourceConfig.driver.getName());
                bean.setXaProperties(parseProperties(dataSourceConfig.properties));
                abstractDataSourceBean = bean;
            } else {
                AtomikosNonXADataSourceBean bean = new AtomikosNonXADataSourceBean();
                bean.setUrl(dataSourceConfig.url);
                bean.setPassword(dataSourceConfig.password);
                bean.setUser(dataSourceConfig.user);
                bean.setDriverClassName(dataSourceConfig.driver.getName());
                abstractDataSourceBean = bean;
            }
            abstractDataSourceBean.setBorrowConnectionTimeout(dataSourceConfig.borrowConnectionTimeout);
            if (-1 != dataSourceConfig.defaultIsolationLevel) {
                abstractDataSourceBean.setDefaultIsolationLevel(dataSourceConfig.defaultIsolationLevel);
            }
            abstractDataSourceBean.setLoginTimeout(dataSourceConfig.loginTimeout);
            abstractDataSourceBean.setMaintenanceInterval(dataSourceConfig.maintenanceInterval);
            abstractDataSourceBean.setMaxIdleTime(dataSourceConfig.maxIdleTime);
            abstractDataSourceBean.setMaxPoolSize(dataSourceConfig.maxSize);
            abstractDataSourceBean.setMinPoolSize(dataSourceConfig.minSize);
            if (dataSourceConfig.poolSize != 0) {
                abstractDataSourceBean.setPoolSize(dataSourceConfig.poolSize);
            }
            abstractDataSourceBean.setReapTimeout(dataSourceConfig.reapTimeout);
            abstractDataSourceBean.setTestQuery(dataSourceConfig.testQuery);
            abstractDataSourceBean.setUniqueResourceName(dataSourceConfig.id);
            abstractDataSourceBean.init();
            for (String key : dataSourceConfig.keys.split(" ")) {
                resourceRegistry.registerResource(DataSource.class, key, abstractDataSourceBean);
            }
            dataSourceBeans.add(abstractDataSourceBean);
        }
        
    }
    
    @Destroy
    public void stop() {
        if (dataSourceBeans != null) {
            for (AbstractDataSourceBean dataSourceBean : dataSourceBeans) {
                dataSourceBean.close();
            }
        }
    }
    
    private Properties parseProperties(String properties) {
        
        Properties prop = new Properties();
        if (properties != null) {
            final StringTokenizer tokenizer = new StringTokenizer(properties, " \t\n\r\f,");
            while (tokenizer.hasMoreElements()) {
                String[] keyValuePair = tokenizer.nextToken().toString().split("=");
                prop.put(keyValuePair[0], keyValuePair[1]);
            }
        }
        return prop;
    }
    
    

}
