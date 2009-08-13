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
package org.sca4j.eclipse.logging;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

import org.sca4j.api.annotation.logging.LogLevel;
import org.sca4j.api.annotation.logging.LogLevels;
import org.sca4j.monitor.MonitorFactory;

/**
 * @version $Rev: 4348 $ $Date: 2008-05-25 21:16:48 +0100 (Sun, 25 May 2008) $
 */
public class EclipseMonitorFactory implements MonitorFactory {
    private final ILog log;
    private final String pluginId;
    private final boolean debug;
    private final String bundleName;
    private final Map<Class<?>, WeakReference<?>> proxies = new WeakHashMap<Class<?>, WeakReference<?>>();

    public EclipseMonitorFactory(ILog log, String pluginId, boolean debug) {
        this.log = log;
        this.pluginId = pluginId;
        this.debug = debug;
        bundleName = "f3";
    }

    public synchronized <T> T getMonitor(Class<T> monitorInterface, URI componentId) {
        return getMonitor(monitorInterface);
    }

    public synchronized <T> T getMonitor(Class<T> monitorInterface) {
        T proxy = getCachedMonitor(monitorInterface);
        if (proxy == null) {
            proxy = createMonitor(monitorInterface);
            proxies.put(monitorInterface, new WeakReference<T>(proxy));
        }
        return proxy;
    }

    protected <T> T getCachedMonitor(Class<T> monitorInterface) {
        WeakReference<?> ref = proxies.get(monitorInterface);
        return (ref != null) ? monitorInterface.cast(ref.get()) : null;
    }

    protected <T> T createMonitor(Class<T> monitorInterface) {
        Method[] methods = monitorInterface.getMethods();
        Map<Method, MethodHandler> handlers = new ConcurrentHashMap<Method, MethodHandler>(methods.length);
        for (Method method : methods) {
            int level = getLevel(method);
            if (debug || level != Status.OK) {
                int code = method.hashCode(); // TODO do we need a way to define specific codes for methods?
                String format = getMessage(monitorInterface, method);
                int throwable = getThrowableParameter(method);
                MethodHandler handler = new MethodHandler(log, level, pluginId, code, format, throwable);
                handlers.put(method, handler);
            }
        }
        InvocationHandler handler = new LoggingHandler(handlers);
        Object proxy = Proxy.newProxyInstance(monitorInterface.getClassLoader(), new Class<?>[]{monitorInterface}, handler);
        return monitorInterface.cast(proxy);
    }

    private int getLevel(Method method) {
        LogLevels levels = LogLevels.getAnnotatedLogLevel(method);
        return translateLogLevel(levels);
    }
    
    private int translateLogLevel(LogLevels levels) {
        
        int result = Status.OK;
        
        if (levels != null) {
            try {
                Level level = Level.parse(levels.toString());
                if(level != null) {
                    if (level.intValue() >= Level.SEVERE.intValue()) {
                        result = Status.ERROR;
                    } else if (level.intValue() >= Level.WARNING.intValue()) {
                        result = Status.WARNING;
                    } else if (level.intValue() >= Level.INFO.intValue()) {
                        result = Status.INFO;
                    }                   
                }                 
            } catch (IllegalArgumentException e) {
                //TODO: Add error reporting for unsupported log level
            }
        } 

        return result;
    }    
    
    private int getThrowableParameter(Method method) {
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> paramType = method.getParameterTypes()[i];
            if (Throwable.class.isAssignableFrom(paramType)) {
                return i;
            }
        }
        return -1;
    }

    private String getMessage(Class<?> monitorInterface, Method method) {
        ResourceBundle bundle = locateBundle(monitorInterface, bundleName);
        String key = monitorInterface.getName() + '#' + method.getName();
        String message = null;
        if (bundle != null) {
            try {
                message = bundle.getString(key);
            } catch (MissingResourceException e) {
                // drop through
            }
        }
        if (message == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(key);
            int argCount = method.getParameterTypes().length;
            if (argCount > 0) {
                builder.append(": {0}");
                for (int i = 1; i < argCount; i++) {
                    builder.append(' ');
                    builder.append('{');
                    builder.append(Integer.toString(i));
                    builder.append('}');
                }
            }
            message = builder.toString();
        }
        return message;
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

    private static class MethodHandler {
        private final ILog logger;
        private final int level;
        private final String pluginId;
        private final int code;
        private final String format;
        private final int throwable;

        private MethodHandler(ILog logger, int level, String pluginId, int code, String format, int throwable) {
            this.logger = logger;
            this.level = level;
            this.pluginId = pluginId;
            this.code = code;
            this.format = format;
            this.throwable = throwable;
        }

        private void invoke(Object[] args) {
            String message = MessageFormat.format(format, args);
            Throwable exception = args != null && throwable >= 0 ? (Throwable) args[throwable] : null;
            Status status = new Status(level, pluginId, code, message, exception);
            logger.log(status);
        }
    }

    private static class LoggingHandler implements InvocationHandler {
        private final Map<Method, MethodHandler> handlers;

        public LoggingHandler(Map<Method, MethodHandler> handlers) {
            this.handlers = handlers;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MethodHandler methodHandler = handlers.get(method);
            if (methodHandler != null) {
                methodHandler.invoke(args);
            }
            return null;
        }
    }

    public void setBundleName(String arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setConfiguration(Properties arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setDefaultLevel(Level arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setLevels(Properties arg0) {
        // TODO Auto-generated method stub
        
    }
}
