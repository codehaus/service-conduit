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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.ejb.Ejb3Configuration;
import org.osoa.sca.annotations.Reference;

import org.sca4j.jpa.spi.EmfBuilderException;
import org.sca4j.jpa.spi.delegate.EmfBuilderDelegate;
import org.sca4j.resource.jndi.proxy.jdbc.DataSourceProxy;
import org.sca4j.spi.resource.DataSourceRegistry;
import org.sca4j.spi.services.synthesize.ComponentRegistrationException;
import org.sca4j.spi.services.synthesize.ComponentSynthesizer;

/**
 * @version $Revision$ $Date$
 */
public class HibernateDelegate implements EmfBuilderDelegate {

    private DataSourceRegistry dataSourceRegistry;
    private ComponentSynthesizer synthesizer;

    @Reference
    public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }

    @Reference
    public void setSynthesizer(ComponentSynthesizer synthesizer) {
        this.synthesizer = synthesizer;
    }

    public EntityManagerFactory build(PersistenceUnitInfo info, ClassLoader classLoader, String dataSourceName) throws EmfBuilderException {

        Ejb3Configuration cfg = new Ejb3Configuration();

        if (dataSourceName != null) {
            DataSource dataSource = dataSourceRegistry.getDataSource(dataSourceName);
            if (dataSource == null) {
                dataSource = mapDataSource(dataSourceName, dataSourceName);
            }
            cfg.setDataSource(dataSource);
        }
        cfg.configure(info, Collections.emptyMap());

        return cfg.buildEntityManagerFactory();
    }

    /**
     * Maps a datasource from JNDI to a SCA4J system component. This provides the defaulting behavior where a user does not have to explicitly
     * configure a SCA4J DataSourceProxy when deploying to a managed environment that provides its own datasources.
     * <p/>
     * This mapping is done by creating a DataSourceProxy component dynamically, registering it with the DataSourceRegistry using the JNDI name as a
     * key, and adding it as a system component. Since the defaulting behavior derives the key from the JNDI name, a datasource is only mapped to a
     * sngle key. If a datasource must be mapped to multiple keys, manual configuration of a DataSourceProxy must be done.
     *
     * @param datasource      the datasource name
     * @param persistenceUnit the persistence unit the datasource is found in
     * @return a proxy to the datasource bound to the JNDI name
     * @throws DataSourceInitException if an error mapping the datasource is encountered
     */
    private DataSource mapDataSource(String datasource, String persistenceUnit) throws DataSourceInitException {
        DataSourceProxy proxy = new DataSourceProxy();
        proxy.setDataSourceRegistry(dataSourceRegistry);
        try {
            proxy.setJndiName(datasource);
            List<String> keys = new ArrayList<String>();
            keys.add(datasource);
            proxy.setDataSourceKeys(keys);
            proxy.init();
            synthesizer.registerComponent(datasource + "Component", DataSource.class, proxy, false);
            return proxy;
        } catch (NamingException e) {
            throw new DataSourceInitException("Datasource " + datasource + " specified in persistent unit " + persistenceUnit
                    + " was not found. The datasource must either be explicitly declared as part of the SCA4J system configuration or provided"
                    + " via JNDI using the name of the data source.", e);
        } catch (ComponentRegistrationException e) {
            throw new DataSourceInitException("Error registering datasource " + datasource + " specified in persistent unit " + persistenceUnit, e);
        }
    }

}
