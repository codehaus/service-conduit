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
package org.sca4j.host;

import java.io.PrintWriter;

/**
 * The root checked exception for the SCA4J runtime.
 *
 * @version $Rev: 4263 $ $Date: 2008-05-18 06:07:03 +0100 (Sun, 18 May 2008) $
 */
public abstract class SCA4JException extends Exception {
    private static final long serialVersionUID = -7847121698339635268L;
    private final String identifier;

    /**
     * Override constructor from Exception.
     *
     * @see Exception
     */
    public SCA4JException() {
        super();
        this.identifier = null;
    }

    /**
     * Override constructor from Exception.
     *
     * @param message passed to Exception
     * @see Exception
     */
    public SCA4JException(String message) {
        super(message);
        this.identifier = null;
    }

    /**
     * Override constructor from Exception.
     *
     * @param message    passed to Exception
     * @param identifier additional error information referred to in the error message
     * @see Exception
     */
    public SCA4JException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    /**
     * Override constructor from Exception.
     *
     * @param message passed to Exception
     * @param cause   passed to Exception
     * @see Exception
     */
    public SCA4JException(String message, Throwable cause) {
        super(message, cause);
        this.identifier = null;
    }

    /**
     * Override constructor from Exception.
     *
     * @param message    passed to Exception
     * @param identifier additional error information referred to in the error message
     * @param cause      passed to Exception
     * @see Exception
     */
    public SCA4JException(String message, String identifier, Throwable cause) {
        super(message, cause);
        this.identifier = identifier;
    }

    /**
     * Override constructor from Exception.
     *
     * @param cause passed to Exception
     * @see Exception
     */
    public SCA4JException(Throwable cause) {
        super(cause);
        this.identifier = null;
    }

    /**
     * Returns a string representing additional error information referred to in the error message.
     *
     * @return additional error information
     */
    public String getIdentifier() {
        return identifier;
    }

    protected void printStackTraceElements(PrintWriter writer) {
        for (StackTraceElement element : getStackTrace()) {
            writer.print("\tat ");
            writer.println(element);
        }
    }
}
