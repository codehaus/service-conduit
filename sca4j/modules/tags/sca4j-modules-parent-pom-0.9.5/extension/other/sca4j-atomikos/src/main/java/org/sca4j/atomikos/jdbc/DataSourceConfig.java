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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataSourceConfig {
    
    @XmlAttribute public String id; // Unique id for this datasource
    public Class<?> driver; // JDBC driver or XA datasource name
    public String url; // URL for non-XA datasource
    public String keys; // Resource keys under which this datasource is available
    public String user; // User for non-XA datasource
    public String password; // Password for non-XA datasource
    public int minSize = 1; // Minnimum number of connections in the pool
    public int maxSize = 10; // Maximum number for connections in the pool
    public String properties; // Properties for XA datasource
    public int borrowConnectionTimeout = 30; // Set minimum and maximum to the same
    public int defaultIsolationLevel = -1; // Timeout in seconds on borrow
    public int maintenanceInterval = 60; // Naintenance interval in seconds
    public int maxIdleTime = 60; // Seconds a connection can stay idle in the pool
    public int poolSize; // Sets minimum and maximum to the same
    public int reapTimeout = 0; // Amount of time a connection can be borrowed
    public String testQuery; // Query executed for testing connections
    public int loginTimeout = 10; // Login timeout in seconds

}
