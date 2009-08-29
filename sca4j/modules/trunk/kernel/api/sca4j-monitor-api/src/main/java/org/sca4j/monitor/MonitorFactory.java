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
package org.sca4j.monitor;

import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;

/**
 * A MonitorFactory creates implementations of components' monitor interfaces that interface with a its monitoring scheme. For example, a
 * implementation may create versions that emit appropriate logging events or which send notifications to a management API.
 *
 * @version $Rev: 4117 $ $Date: 2008-05-04 08:00:12 +0100 (Sun, 04 May 2008) $
 */
public interface MonitorFactory {
    /**
     * Return a monitor for a monitor interface.
     *
     * @param monitorInterface the monitoring interface
     * @return an implementation of the monitoring interface; will not be null
     */
    <T> T getMonitor(Class<T> monitorInterface);

    /**
     * Return a monitor for a component's monitor interface.
     *
     * @param monitorInterface the component's monitoring interface
     * @param componentId      the specific component to monitor
     * @return an implementation of the monitoring interface; will not be null
     */
    <T> T getMonitor(Class<T> monitorInterface, URI componentId);

    void setConfiguration(Properties configuration);

    /**
     * Sets the definition of custom levels for specific monitored methods, may be null or empty
     *
     * @param levels definition of custom levels for specific monitored methods, may be null or empty
     */
    void setLevels(Properties levels);

    /**
     * Sets the default log level to use.
     *
     * @param defaultLevel the default log level to use
     */
    void setDefaultLevel(Level defaultLevel);

    /**
     * Sets the name of a resource bundle that will be used.
     *
     * @param bundleName the name of a resource bundle that will be passed to the logger
     */
    void setBundleName(String bundleName);
    
}
