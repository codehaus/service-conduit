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
package org.sca4j.jpa.hibernate;

import java.util.*;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.jpa.spi.delegate.EmfBuilderDelegate;
import org.sca4j.jndi.datasource.JndiDataSourceProxy;
import org.sca4j.jndi.config.JndiResourceConfig;
import org.sca4j.spi.resource.ResourceRegistry;
import org.sca4j.spi.services.synthesize.ComponentRegistrationException;
import org.sca4j.spi.services.synthesize.ComponentSynthesizer;

/**
 * @version $Revision$ $Date$
 */
public class HibernateDelegate implements EmfBuilderDelegate {

    private ResourceRegistry resourceRegistry;
    private ComponentSynthesizer synthesizer;

    @Reference
    public void setResourceRegistry(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    @Reference
    public void setSynthesizer(ComponentSynthesizer synthesizer) {
        this.synthesizer = synthesizer;
    }

    public EntityManagerFactory build(PersistenceUnitInfo info, ClassLoader classLoader, String dataSourceName) {

    	Ejb3Configuration cfg = new Ejb3Configuration();

        if (dataSourceName != null) {
            DataSource dataSource = searchResources(dataSourceName);
            if (dataSource == null) {
                dataSource = mapDataSource(dataSourceName, dataSourceName);
            }
            cfg.setDataSource(dataSource);
        }
        
        cfg.configure(info, Collections.emptyMap());
        return cfg.buildEntityManagerFactory();
        
    }

    public Object getDelegate(EntityManagerFactory entityManagerFactory) {
        // TODO Auto-generated method stub
        return ((EntityManagerFactoryImpl) entityManagerFactory).getSessionFactory();
    }
    
    private DataSource mapDataSource(String datasource, String persistenceUnit) {
        JndiDataSourceProxy proxy = new JndiDataSourceProxy();         
        proxy.setResourceRegistry(resourceRegistry);
        try {
            JndiResourceConfig resourceConfig = new JndiResourceConfig();
            resourceConfig.jndiName = datasource;
            List<String> keys = new ArrayList<String>();
            keys.add(datasource);
            proxy.setDataSourceKeys(keys);
            proxy.init(proxy.getResourceConfig());
            synthesizer.registerComponent(datasource + "Component", DataSource.class, proxy, false);
            return proxy;
        } catch (NamingException e) {
            throw new AssertionError("Datasource " + datasource + " specified in persistent unit " + persistenceUnit
                    + " was not found. The datasource must either be explicitly declared as part of the SCA4J system configuration or provided"
                    + " via JNDI using the name of the data source.");
        } catch (ComponentRegistrationException e) {
            throw new AssertionError("Error registering datasource " + datasource + " specified in persistent unit " + persistenceUnit);
        }
    }

    /**
     * Retrieve data source registered is resource repository under dataSourceName, if not found perform JNDI lookup using
     * dataSourceName as JNDI name and register in resource repository
     * @param dataSourceName  data source name or JNDI name
     * @return DataSource data source
     */
    private DataSource searchResources(String dataSourceName) {
        DataSource dataSource = resourceRegistry.getResource(DataSource.class, dataSourceName);

        if (dataSource == null) {
            Collection<DataSource> dataSources = resourceRegistry.getResources(DataSource.class);
            for (DataSource ds : dataSources) {
                if (ds instanceof JndiDataSourceProxy) {
                   if (((JndiDataSourceProxy) ds).getResourceConfig().jndiName.equals(dataSourceName)) {
                       dataSource = ds;
                       break;
                   }
                }
            }
        }

        return dataSource;
    }

}
