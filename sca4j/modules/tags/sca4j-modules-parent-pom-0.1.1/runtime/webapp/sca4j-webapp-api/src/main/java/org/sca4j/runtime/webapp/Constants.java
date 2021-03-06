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
package org.sca4j.runtime.webapp;

/**
 * Constants used by the web application booter
 *
 * @version $Rev: 5335 $ $Date: 2008-09-06 12:14:49 +0100 (Sat, 06 Sep 2008) $
 */
public final class Constants {

    /**
     * Default management domain.
     */
    public static final String DEFAULT_MANAGEMENT_DOMAIN = "webapp-host";

    /**
     * Name of the servlet context-param that should contain the JMX management domain.
     */
    public static final String MANAGEMENT_DOMAIN_PARAM = "sca4j.management.domain";

    /**
     * Name of the servlet context-param that should contain the component id for the webapp.
     */
    public static final String DOMAIN_PARAM = "sca4j.domain";

    /**
     * Name of the servlet context-param that should contain the component target namespace for the webapp.
     */
    public static final String COMPOSITE_NAMESPACE_PARAM = "sca4j.compositeNamespace";

    /**
     * Name of the servlet context-param that should contain the component id for the webapp.
     */
    public static final String COMPOSITE_PARAM = "sca4j.composite";

    /**
     * Servlet context-param name for the base runtime directory.
     */
    public static final String BASE_DIR = "sca4j.baseDir";

    /**
     * Name of the servlet context-param that should contain the component id for the webapp.
     */
    public static final String COMPONENT_PARAM = "sca4j.component";

    /**
     * Servlet context-param name for user-specified application SCDL path.
     */
    public static final String APPLICATION_SCDL_PATH_PARAM = "sca4j.applicationScdlPath";

    /**
     * Default application SCDL path.
     */
    public static final String APPLICATION_SCDL_PATH_DEFAULT = "/WEB-INF/web.composite";

    /**
     * Servlet context-param name for setting if the runtime is online.
     */
    public static final String ONLINE_PARAM = "sca4j.online";

    /**
     * Name of the context attribute that contains the ComponentContext.
     */
    public static final String CONTEXT_ATTRIBUTE = "org.osoa.sca.ComponentContext";

    /**
     * Name of the parameter that defines the class to load to launch the runtime.
     */
    public static final String RUNTIME_PARAM = "sca4j.runtimeImpl";

    /**
     * Name of the default webapp runtime implementation.
     */
    public static final String RUNTIME_DEFAULT = "org.sca4j.runtime.webapp.WebappRuntimeImpl";

    /**
     * Name of the parameter that defines whether the work scheduler should pause on start.
     */
    public static final String PAUSE_ON_START_PARAM = "sca4j.work.scheduler.pauseOnStart";

    /**
     * The default pause on start value.
     */
    public static final String PAUSE_ON_START_DEFAULT = "false";

    /**
     * Name of the parameter that defines the number of worker threads.
     */
    public static final String NUM_WORKERS_PARAM = "sca4j.work.scheduler.numWorkers";

    /**
     * The number of default worker threads.
     */
    public static final String NUM_WORKERS_DEFAULT = "10";

    /**
     * Name of the parameter that defines the class to load to bootstrap the runtime.
     */
    public static final String BOOTSTRAP_PARAM = "sca4j.bootstrapImpl";

    /**
     * Name of the default webapp bootstrap implementation.
     */
    public static final String BOOTSTRAP_DEFAULT = "org.sca4j.fabric.runtime.bootstrap.ScdlBootstrapperImpl";

    /**
     * Name of the parameter that defines the class to load to coordinate booting the runtime.
     */
    public static final String COORDINATOR_PARAM = "sca4j.coordinatorImpl";

    /**
     * Name of the default webapp coordinator implementation.
     */
    public static final String COORDINATOR_DEFAULT = "org.sca4j.fabric.runtime.DefaultCoordinator";


    /**
     * Servlet context-param name for user-specified system SCDL path.
     */
    public static final String SYSTEM_SCDL_PATH_PARAM = "sca4j.systemScdlPath";

    /**
     * Default webapp system SCDL path.
     */
    public static final String SYSTEM_SCDL_PATH_DEFAULT = "META-INF/sca4j/webapp.composite";

    /**
     * Servlet context-param name for user-specified intents file path.
     */
    public static final String INTENTS_PATH_PARAM = "sca4j.intentsPath";

    /**
     * Default webapp system intents file path.
     */
    public static final String INTENTS_PATH_DEFAULT = "META-INF/sca4j/intents.xml";

    /**
     * Context attribute to which the SCA4J runtime for this servlet context is stored.
     */
    public static final String RUNTIME_ATTRIBUTE = "sca4j.runtime";

    /**
     * Servlet context-param name for the path to the composite to set as the webb app composite
     */
    public static final String CURRENT_COMPOSITE_PATH_PARAM = "sca4j.currentCompositePath";

    /**
     * Servlet context-param name for system monitoring level. Supported values are the names of statics defined in java.util.logging.Level.
     */
    public static final String SYSTEM_MONITORING_PARAM = "sca4j.monitoringLevel";

    /**
     * Default log level
     */
    public static final String SYSTEM_MONITORING_DEFAULT = "FINEST";

    /**
     * Name of the parameter that defines the class to load to launch the runtime.
     */
    public static final String MONITOR_FACTORY_PARAM = "sca4j.monitorFactory";

    /**
     * Name of the default webapp runtime implementation.
     */
    public static final String MONITOR_FACTORY_DEFAULT = "org.sca4j.monitor.impl.JavaLoggingMonitorFactory";

    /**
     * Parameter that defines the log formatter
     */
    public static final String LOG_FORMATTER_PARAM = "sca4j.jdkLogFormatter";

    /**
     * Name of the default log formatter
     */
    public static final String LOG_FORMATTER_DEFAULT = "org.sca4j.monitor.impl.SCA4JLogFormatter";
    /**
     * Name of bundle files used for monitoring messages
     */
    public static final String MONITORING_BUNDLE_PARAM = "sca4j.monitoringBundle";

    /**
     * Default name of bundle files for monitoring messages
     */
    public static final String MONITORING_BUNDLE_DEFAULT = "f3";

    private Constants() {
    }
}
