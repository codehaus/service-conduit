/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
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
package org.sca4j.monitor.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;

import org.sca4j.monitor.MonitorFactory;

/**
 * Implementation of a {@link MonitorFactory} that produces implementations that simply
 * return.
 *
 * @version $Rev: 4117 $ $Date: 2008-05-04 08:00:12 +0100 (Sun, 04 May 2008) $
 */
public class NullMonitorFactory implements MonitorFactory {

    /**
     * Singleton hander that does nothing.
     */
    private static final InvocationHandler NULL_MONITOR = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) {
            return null;
        }
    };

    public <T> T getMonitor(Class<T> monitorInterface) {
        /*
         * This uses a reflection proxy to implement the monitor interface which
         * is a simple but perhaps not very performant solution. Performance
         * might be improved by code generating an implementation with empty methods.
         */
        Object proxy = Proxy.newProxyInstance(monitorInterface.getClassLoader(),
                                              new Class<?>[]{monitorInterface},
                                              NULL_MONITOR);
        return monitorInterface.cast(proxy);
    }

    public <T> T getMonitor(Class<T> monitorInterface, URI componentId) {
        return getMonitor(monitorInterface);
    }

    public void setConfiguration(Properties configuration) {

    }

    public void setLevels(Properties levels) {

    }

    public void setDefaultLevel(Level defaultLevel) {

    }

    public void setBundleName(String bundleName) {

    }
}
