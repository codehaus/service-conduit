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
package org.sca4j.runtime.generic.junit;

import java.net.URI;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import junit.framework.TestCase;

import org.sca4.runtime.generic.impl.GenericRuntimeImpl;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.monitor.impl.JavaLoggingMonitorFactory;

/**
 * Abstract super class for all JUnit test.
 *
 */
public class AbstractScaTest extends TestCase {
    
    private GenericRuntimeImpl genericRuntime;
    
    /**
     * Initialises the test with the application SCDL.
     * @param applicationScdl Application SCDL.
     */
    public AbstractScaTest(String applicationScdl) {
        genericRuntime = new GenericRuntimeImpl(URI.create(""), System.getProperties(), getMonitorFactory(), getMBeanServer());
        genericRuntime.boot();
        genericRuntime.contriute(applicationScdl);
    }
    
    /**
     * Shuts down the runtime.
     */
    protected void shutdown() {
        genericRuntime.shutdown();
    }
    
    /**
     * Gets a service proxy.
     * 
     * @param <T> Type of the service proxy.
     * @param serviceClass Service proxy class.
     * @param serviceName Service name.
     * @return Service proxy instance.
     */
    protected <T> T getServiceProxy(Class<T> serviceClass, String serviceName) {
        return genericRuntime.getServiceProxy(serviceClass, serviceName);
    }
    
    /**
     * Override if you want to use a different monitor factory.
     * @return JDK 1.4 logging monitor factory.
     */
    protected MonitorFactory getMonitorFactory() {
        return new JavaLoggingMonitorFactory();
    }
    
    /**
     * Override if you want to provide additionalhost proeprties.
     * @return System properties.
     */
    protected Properties getHostProperties() {
        return System.getProperties();
    }
    
    /**
     * Override if you want to provide a different MBean server.
     * @return Default platform MBean server.
     */
    protected MBeanServer getMBeanServer() {
        return MBeanServerFactory.newMBeanServer();
    }

}
