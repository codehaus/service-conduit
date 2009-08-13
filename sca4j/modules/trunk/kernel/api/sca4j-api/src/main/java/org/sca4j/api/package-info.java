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

/**
 * Overview of sca4j Application Programming Interface.
 *
 * This package contains classes and annotations intended for use by application code.
 * In general, the programming models supported by sca4j are designed to be
 * non-intrusive. The goal is for application code to be portable to any framework
 * supporting an Inversion of Control style programming model and as such does
 * not require application code to inherit from framework classes or to implement
 * framework specific interfaces. Where additional information is required by the
 * framework, Java annotations are used.
 *
 * <h1>Monitoring Framework</h1>
 *
 * sca4j provides a monitoring framework that application code can use to
 * send management events to the host's logging framework in a manner that is
 * independent of the actual framework used by the runtime environment. This is
 * the same infrastructure as used by sca4j itself, allowing events from
 * applications, the sca4j runtime and the host itself to all be handled
 * by the host's logging infrastructure. These events can be combined in one stream
 * or seperated as supported by the host infrastructure.
 *
 * To use this framework, application code should define a component-specific interface
 * defining the monitoring events that it wishes to send and mark a field, setter or
 * constructor with the {@link org.sca4j.api.annotation.Monitor @Monitor} annotation.
 * The framework will inject a implementation of the monitoring interface that dispatches
 * events to the underlying infrastructure.
 *
 * <pre>
 * public class MyComponent {
 *     private final MyComponentMonitor monitor;
 *
 *     public MyComponent(@Monitor MyComponentMonitor monitor) {
 *         this.monitor = monitor;
 *     }
 *
 *     public void start() {
 *         monitor.started();
 *     }
 *
 *     public void stop() {
 *         monitor.stopped();
 *     }
 *
 *     public interface MyComponentMonitor {
 *         void started();
 *         void stopped();
 *     }
 * }
 * </pre>
 *
 * The {@link org.sca4j.api.annotation.LogLevel @LogLevel} annotation
 * can be used to provide a hint for the logging level to associate with the event.
 * The actual level used is determined by the configuration of the monitoring framework.
 *
 * For performance reasons, the objects passed as parameters should typically be instances
 * that are already in use by the application code; operations that allocate new objects
 * (such as string concatenation) should be avoided.
 *
 * <h1></h1> 
 */
package org.sca4j.api;
