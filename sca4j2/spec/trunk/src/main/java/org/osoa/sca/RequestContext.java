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
package org.osoa.sca;

import javax.security.auth.Subject;

/**
 * Interface that provides information on the current request.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
public interface RequestContext {
    /**
     * Returns the JAAS Subject of the current request.
     *
     * @return the Subject of the current request
     */
    Subject getSecuritySubject();

    /**
     * Returns the name of the service that was invoked.
     *
     * @return the name of the service that was invoked
     */
    String getServiceName();

    /**
     * Returns a CallableReference for the service that was invoked by the caller.
     *
     * @param <B> the Java type of the business interface for the reference
     * @return a CallableReference for the service that was invoked by the caller
     */
    <B> CallableReference<B> getServiceReference();

    /**
     * Returns a type-safe reference to the callback provided by the caller.
     *
     * @param <CB> the Java type of the business interface for the callback
     * @return a type-safe reference to the callback provided by the caller
     */
    <CB> CB getCallback();

    /**
     * Returns a CallableReference to the callback provided by the caller.
     *
     * @param <CB> the Java type of the business interface for the callback
     * @return a CallableReference to the callback provided by the caller
     */
    <CB> CallableReference<CB> getCallbackReference();
}
