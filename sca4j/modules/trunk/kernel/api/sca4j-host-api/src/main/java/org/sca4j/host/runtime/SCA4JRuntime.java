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
package org.sca4j.host.runtime;

import java.net.URI;

import javax.management.MBeanServer;

import org.sca4j.monitor.MonitorFactory;

/**
 * Represents a node in the service network. Runtimes may host components and/or function as a Domain controller.
 *
 * @version $Rev: 5395 $ $Date: 2008-09-13 14:35:47 +0100 (Sat, 13 Sep 2008) $
 */
public interface SCA4JRuntime<HI extends HostInfo> extends RuntimeLifecycle {

    /**
     * Returns the host ClassLoader that is parent to all SCA4J classloaders.
     *
     * @return the host's ClassLoader
     */
    ClassLoader getHostClassLoader();

    /**
     * Returns the type of info supplied by the host.
     *
     * @return the type of info supplied by the host
     */
    Class<HI> getHostInfoType();

    /**
     * Returns the info this runtime will make available to service components.
     *
     * @return the info this runtime will make available to service components
     */
    HI getHostInfo();

    /**
     * Sets the info this runtime should make available to service components.
     *
     * @param hostInfo the information this runtime should make available to service components
     */
    void setHostInfo(HI hostInfo);

    /**
     * Returns the MonitorFactory that this runtime is using.
     *
     * @return the MonitorFactory that this runtime is using
     */
    MonitorFactory getMonitorFactory();

    /**
     * Sets the MonitorFactory that this runtime should use.
     *
     * @param monitorFactory the MonitorFactory that this runtime should use
     */
    void setMonitorFactory(MonitorFactory monitorFactory);

    /**
     * Returns the MBeanServer this runtime should use.
     *
     * @return the MBeanServer
     */
    MBeanServer getMBeanServer();

    /**
     * Sets the MBeanServer this runtime should use.
     * <p/>
     * This allows the host environment to specify an MBeanServer with which any manageable runtime components should be registered.
     *
     * @param mbServer the MBeanServer this runtime should use
     */
    void setMBeanServer(MBeanServer mbServer);

    /**
     * Returns the JMX sub domain this runtime should use.
     *
     * @return the JMX sub domain this runtime should use
     */
    String getJMXSubDomain();

    /**
     * Sets the JMX sub domain this runtime should use.
     * <p/>
     * This will be used as the sub domain portion of the ObjectName for all MBeans registered with the MBeanServer
     *
     * @param jmxSubDomain the JMX domain this runtime should use
     */
    void setJmxSubDomain(String jmxSubDomain);

    /**
     * Initialize a runtime. An initialized runtime has has completed core service initialization, recovery operations, and is ready to be started.
     *
     * @throws InitializationException if there is an error initializing the runtime
     */
    void bootSystem() throws InitializationException;

    /**
     * Starts the runtime. A runtime is ready to process requests when it has been started.
     *
     * @throws StartException if there is an error starting the runtime
     */
    void start() throws StartException;

    /**
     * Returns the system component providing the designated service.
     *
     * @param service the service interface required
     * @param uri     the id of the system component
     * @param <I>     the Java type for the service interface
     * @return an implementation of the requested service
     */
    <I> I getSystemComponent(Class<I> service, URI uri);

    /**
     * Boots the runtime with its primordial components.
     *
     * @throws InitializationException if an error occurs booting the runtime
     */
    void bootPrimordial(BootConfiguration configuration) throws InitializationException;

    /**
     * Join the domain in a non-blocking fashion.
     *
     * @param timeout the timeout in milliseconds or -1 if the operation should wait indefinitely
     * @return a future that can be polled for completion of the operation
     * @throws InitializationException if an error occurs joining the domain
     */
    void joinDomain(long timeout) throws InitializationException;

    /**
     * Shuts the runtime down, stopping it from receiving requests and detaching it from the domain. In-flight synchronous operations will be allowed
     * to proceed to completion.
     *
     * @return a future that can be polled for completion of the operation
     * @throws ShutdownException if an error ocurrs shutting down the runtime
     */
    void shutdown();

}
