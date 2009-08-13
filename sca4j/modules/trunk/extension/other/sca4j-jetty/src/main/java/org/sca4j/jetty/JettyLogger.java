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
package org.sca4j.jetty;

import org.mortbay.log.Logger;

/**
 * Serves as a wrapper for a {@link TransportMonitor} to replace Jetty's logging mechanism
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
public class JettyLogger implements Logger {

    private TransportMonitor monitor;
    private boolean debugEnabled;

    public void setMonitor(TransportMonitor monitor) {
        this.monitor = monitor;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void info(String msg, Object arg0, Object arg1) {
        if (monitor != null) {
            monitor.debug(msg, arg0, arg1);
        } else if (debugEnabled) {
            System.err.println(":INFO:  " + format(msg, arg0, arg1));
        }
    }

    public void debug(String msg, Throwable th) {
        if (debugEnabled) {
            if (monitor != null) {
                monitor.debug(msg, th);
            } else {
                System.err.println(":DEBUG:  " + msg);
                th.printStackTrace();
            }
        }
    }

    public void debug(String msg, Object arg0, Object arg1) {
        if (debugEnabled) {
            if (monitor != null) {
                monitor.debug(msg, arg0, arg1);
            } else {
                System.err.println(":DEBUG: " + format(msg, arg0, arg1));
            }
        }
    }

    public void warn(String msg, Object arg0, Object arg1) {
        if (monitor != null) {
            monitor.warn(msg, arg0, arg1);
        } else if (debugEnabled) {
            System.err.println(":WARN: " + format(msg, arg0, arg1));
        }
    }

    public void warn(String msg, Throwable th) {
        if (monitor != null) {
            monitor.warn(msg, th);
        } else if (debugEnabled) {
            System.err.println(":WARN: " + msg);
            th.printStackTrace();
        }
    }

    public Logger getLogger(String name) {
        return this;
    }

    private String format(String msg, Object arg0, Object arg1) {
        int i0 = msg.indexOf("{}");
        int i1 = i0 < 0 ? -1 : msg.indexOf("{}", i0 + 2);
        if (arg1 != null && i1 >= 0) {
            msg = msg.substring(0, i1) + arg1 + msg.substring(i1 + 2);
        }
        if (arg0 != null && i0 >= 0) {
            msg = msg.substring(0, i0) + arg0 + msg.substring(i0 + 2);
        }
        return msg;
    }
}
