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
package org.sca4j.monitor.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.sca4j.api.annotation.logging.LogLevels;
import org.sca4j.monitor.MonitorFactory;

/**
 * A factory for monitors that forwards events to a {@link java.util.logging.Logger Java Logging (JSR47) Logger}.
 *
 * @version $Rev: 5140 $ $Date: 2008-08-02 17:37:52 +0100 (Sat, 02 Aug 2008) $
 * @see java.util.logging
 */
public class JavaLoggingMonitorFactory implements MonitorFactory {
    private Properties levels;
    private Level defaultLevel;
    private String bundleName;
    private final Map<Class<?>, WeakReference<?>> proxies = new WeakHashMap<Class<?>, WeakReference<?>>();
    private Formatter formatter;

    /**
     * Construct a MonitorFactory that will monitor the specified methods at the specified levels and generate messages using java.util.logging.
     * <p/>
     * The supplied Properties can be used to specify custom log levels for specific monitor methods. The key should be the method name in form
     * returned by <code>Class.getName() + '#' + Method.getName()</code> and the value the log level to use as defined by {@link
     * java.util.logging.Level}.
     */
    public JavaLoggingMonitorFactory() {
    }

    public void setLevels(Properties levels) {
        this.levels = levels;
    }

    public void setDefaultLevel(Level defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public <T> T getMonitor(Class<T> monitorInterface, URI componentId) {
        return getMonitor(monitorInterface);
    }

    public void setConfiguration(Properties configuration) {
        String formatterClass = (String) configuration.get("sca4j.jdkLogFormatter");
        if (formatterClass != null) {
            try {
                Class<Formatter> clazz = (Class<Formatter>) Class.forName(formatterClass);
                formatter = clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Invalid formatter class", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid formatter class", e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Invalid formatter class", e);
            }
        }
    }

    public synchronized <T> T getMonitor(Class<T> monitorInterface) {
        T monitor = getCachedMonitor(monitorInterface);
        if (monitor == null) {
            monitor = createMonitor(monitorInterface);
            proxies.put(monitorInterface, new WeakReference<T>(monitor));
        }
        return monitor;
    }

    protected <T> T getCachedMonitor(Class<T> monitorInterface) {
        WeakReference<?> ref = proxies.get(monitorInterface);
        return (ref != null) ? monitorInterface.cast(ref.get()) : null;
    }

    protected <T> T createMonitor(Class<T> monitorInterface) {
        String className = monitorInterface.getName();
        Logger logger = Logger.getLogger(className);
        setFormatter(logger);
        ResourceBundle bundle = locateBundle(monitorInterface, bundleName);

        Method[] methods = monitorInterface.getMethods();
        Map<Method, MethodInfo> methodInfo = new ConcurrentHashMap<Method, MethodInfo>(methods.length);
        for (Method method : methods) {
            String methodName = method.getName();
            String key = className + '#' + methodName;
            
            LogLevels level = getLogLevel(method, key);            
            Level methodLevel = translateLogLevel(level);
            int throwable = getExceptionParameterIndex(method);
            
            MethodInfo info = new MethodInfo(logger, methodLevel, methodName, bundle, throwable);
            methodInfo.put(method, info);
        }

        InvocationHandler handler = new LoggingHandler(methodInfo);
        Object proxy = Proxy.newProxyInstance(monitorInterface.getClassLoader(),
                                              new Class<?>[]{monitorInterface},
                                              handler);
        return monitorInterface.cast(proxy);
    }

    private LogLevels getLogLevel(Method method, String key) {
        LogLevels level = getLogLevelFromConfig(key);            
        if (level == null) {
            level = LogLevels.getAnnotatedLogLevel(method);
        }
        return level;
    }

    private int getExceptionParameterIndex(Method method) {
        int result = -1;
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> paramType = method.getParameterTypes()[i];
            if (Throwable.class.isAssignableFrom(paramType)) {
                result = i;
                break;
            }
        }
        
        //The position in the monitor interface's parameter list of the first throwable
        //is used when creating the LogRecord in the MethodInfo
        return result;
    }

    protected <T> ResourceBundle locateBundle(Class<T> monitorInterface, String bundleName) {
        Locale locale = Locale.getDefault();
        ClassLoader cl = monitorInterface.getClassLoader();
        String packageName = monitorInterface.getPackage().getName();
        while (true) {
            try {
                return ResourceBundle.getBundle(packageName + '.' + bundleName, locale, cl);
            } catch (MissingResourceException e) {
                //ok
            }
            int index = packageName.lastIndexOf('.');
            if (index == -1) {
                break;
            }
            packageName = packageName.substring(0, index);
        }
        try {
            return ResourceBundle.getBundle(bundleName, locale, cl);
        } catch (Exception e) {
            return null;
        }
    }
    
    private Level translateLogLevel(LogLevels level) {
        Level result = null;
        if (level == null) {
            result = defaultLevel;
        } 
        else {
            try {
                //Because the LogLevels' values are based on the Level's logging levels, 
                //no translation is required, just a pass-through mapping
                result = Level.parse(level.toString());
            } catch (IllegalArgumentException e) {
                //TODO: Add error reporting for unsupported log level
                result = defaultLevel;
            }
        }
        return result;
    }

    private LogLevels getLogLevelFromConfig(String key) {
        LogLevels result = null;
        if (levels != null && levels.getProperty(key) != null) {
            try {
                result = Enum.valueOf(LogLevels.class, levels.getProperty(key));                    
            } catch (IllegalArgumentException e) {
                //TODO: Add error reporting for unsupported log level
            }                
        }
        return result;
    }

    private void setFormatter(Logger logger) {
        if (formatter != null) {
            Logger parent = logger.getParent();
            if (parent != null && logger.getUseParentHandlers()) {
                setFormatter(parent);
            } else {
                for (Handler handler : logger.getHandlers()) {
                    handler.setFormatter(formatter);
                }
            }
        }
    }    

    private static class MethodInfo {
        private final Logger logger;
        private final Level level;
        private final String methodName;
        private final ResourceBundle bundle;
        private final int throwable;

        private MethodInfo(Logger logger, Level level, String methodName, ResourceBundle bundle, int throwable) {
            this.logger = logger;
            this.level = level;
            this.methodName = methodName;
            this.bundle = bundle;
            this.throwable = throwable;
        }

        private void invoke(Object[] args) {
            if (level == null || !logger.isLoggable(level)) {
                return;
            }

            // construct the key for the resource bundle
            String className = logger.getName();
            String key = className + '#' + methodName;

            LogRecord logRecord = new LogRecord(level, key);
            logRecord.setLoggerName(className);
            logRecord.setParameters(args);
            if (args != null && throwable >= 0) {
                logRecord.setThrown((Throwable) args[throwable]);
            }
            logRecord.setResourceBundle(bundle);
            logger.log(logRecord);
        }
    }

    private static class LoggingHandler implements InvocationHandler {
        private final Map<Method, MethodInfo> info;

        public LoggingHandler(Map<Method, MethodInfo> methodInfo) {
            this.info = methodInfo;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MethodInfo methodInfo = info.get(method);
            if (methodInfo != null) {
                methodInfo.invoke(args);
            }
            return null;
        }
    }

}
