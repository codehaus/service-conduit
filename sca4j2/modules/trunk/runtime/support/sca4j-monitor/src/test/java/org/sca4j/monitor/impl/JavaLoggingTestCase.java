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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.sca4j.api.annotation.logging.Fine;
import org.sca4j.api.annotation.logging.Info;
import org.sca4j.api.annotation.logging.LogLevel;
import org.sca4j.api.annotation.logging.LogLevels;
import org.sca4j.api.annotation.logging.Severe;
import org.sca4j.api.annotation.logging.Warning;
import org.sca4j.monitor.MonitorFactory;

/**
 * Test case for the JavaLoggingMonitorFactory.
 *
 * @version $Rev: 4339 $ $Date: 2008-05-25 16:15:34 +0100 (Sun, 25 May 2008) $
 */
public class JavaLoggingTestCase extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(Monitor.class.getName());
    private static final MockHandler HANDLER = new MockHandler();

    private MonitorFactory factory;

    /**
     * Smoke test to ensure the LOGGER is working.
     */
    public void testLogger() {
        LOGGER.info("test");
        assertEquals(1, HANDLER.logs.size());
    }

    /**
     * Test that no record is logged.
     */
    public void testUnloggedEvent() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventNotToLog();
        assertEquals(0, HANDLER.logs.size());
    }

    /**
     * Test the correct record is written for an event with no arguments.
     */
    public void testEventWithNoArgs() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithNoArgs();
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals(LOGGER.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithNoArgs", record.getMessage());
    }

    /**
     * Test the correct record is written for an event defined by annotation.
     */
    public void testEventWithInfoAnnotation() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithInfoAnnotation();
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals(LOGGER.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithInfoAnnotation", record.getMessage());
    }
    
    /**
     * Test the correct record is written for an event defined by annotation.
     */
    public void testEventWithSevereAnnotation() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithSevereAnnotation();
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Level.SEVERE, record.getLevel());
        assertEquals(LOGGER.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithSevereAnnotation", record.getMessage());
    }
    
    /**
     * Test the correct record is written for an event defined by annotation.
     */
    public void testEventWithWarningAnnotation() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithWarningAnnotation();
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Level.WARNING, record.getLevel());
        assertEquals(LOGGER.getName(), record.getLoggerName());
        assertEquals(Monitor.class.getName() + "#eventWithWarningAnnotation", record.getMessage());
    }
    
    /**
     * Test the correct record is written for an event defined by annotation.
     */
    public void testEventWithFineAnnotation() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithFineAnnotation();
        //Logger log level is info
        assertEquals(0, HANDLER.logs.size());
    }         

    /**
     * Test the argument is logged.
     */
    public void testEventWithOneArg() {
        Monitor mon = factory.getMonitor(Monitor.class);
        mon.eventWithOneArg("ARG");
        assertEquals(1, HANDLER.logs.size());
        LogRecord record = HANDLER.logs.get(0);
        assertEquals(Monitor.class.getName() + "#eventWithOneArg", record.getMessage());
    }

    protected void setUp() throws Exception {
        super.setUp();
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(HANDLER);
        HANDLER.flush();

        String sourceClass = Monitor.class.getName();
        Properties levels = new Properties();
        levels.setProperty(sourceClass + "#eventWithNoArgs", "INFO");
        levels.setProperty(sourceClass + "#eventWithOneArg", "INFO");
        levels.setProperty(sourceClass + "#eventWithThrowable", "WARNING");
        factory = new JavaLoggingMonitorFactory();
        factory.setLevels(levels);
        factory.setDefaultLevel(Level.FINE);
        factory.setBundleName("TestMessages");
    }

    protected void tearDown() throws Exception {
        LOGGER.removeHandler(HANDLER);
        HANDLER.flush();
        super.tearDown();
    }

    /**
     * Mock log HANDLER to capture records.
     */
    public static class MockHandler extends Handler {
        List<LogRecord> logs = new ArrayList<LogRecord>();

        public void publish(LogRecord record) {
            logs.add(record);
        }

        public void flush() {
            logs.clear();
        }

        public void close() throws SecurityException {
        }
    }

    @SuppressWarnings({"JavaDoc"})
    public static interface Monitor {
        void eventNotToLog();

        @LogLevel(LogLevels.INFO)
        void eventWithNoArgs();

        @LogLevel(LogLevels.INFO)
        void eventWithOneArg(String msg);

        @LogLevel(LogLevels.WARNING)
        void eventWithThrowable(Exception e);

        @Info
        void eventWithInfoAnnotation();
        
        @Severe
        void eventWithSevereAnnotation();
        
        @Warning
        void eventWithWarningAnnotation();
        
        @Fine
        void eventWithFineAnnotation();
    }
}
