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
package org.sca4j.jndi.datasource;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.jndi.config.DataSourceConfig;
import org.sca4j.jndi.config.DataSourceConfigCollection;
import org.sca4j.spi.resource.ResourceRegistry;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * Used for creating JNDI datasources.
 *
 * @author deanb
 *
 */
@EagerInit
public class JndiDataSourceFactory  {

    @Property(required=false) public DataSourceConfigCollection dataSourceConfigCollection;
    @Reference public ResourceRegistry resourceRegistry;

    @Init
    public void init() throws SQLException, NamingException {

        if (dataSourceConfigCollection == null) return;

        for (DataSourceConfig dataSourceConfig : dataSourceConfigCollection.dataSources) {

            JndiDataSourceProxy proxy = new JndiDataSourceProxy();
            proxy.setResourceRegistry(this.resourceRegistry);
            proxy.setDataSourceKeys(Arrays.asList(dataSourceConfig.keys.split(" ")));
            proxy.init(dataSourceConfig);

        }

    }

}