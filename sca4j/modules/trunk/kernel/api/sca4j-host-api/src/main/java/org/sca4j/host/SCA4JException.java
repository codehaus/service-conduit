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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
