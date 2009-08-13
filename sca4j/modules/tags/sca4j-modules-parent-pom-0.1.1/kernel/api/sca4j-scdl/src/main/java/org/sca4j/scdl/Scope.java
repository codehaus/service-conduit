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
package org.sca4j.scdl;

import java.net.URI;

import org.osoa.sca.Conversation;

/**
 * An implementation scope that defines the lifecycle for implementation instances.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 * @param <T> the type of identifier used to identify instances of this scope
 */
public class Scope<T> extends ModelObject {
    private static final long serialVersionUID = -5300929173662672089L;
    public static final Scope<Object> STATELESS = new Scope<Object>("STATELESS", Object.class);
    public static final Scope<Thread> REQUEST = new Scope<Thread>("REQUEST", Thread.class);
    public static final Scope<Conversation> CONVERSATION = new Scope<Conversation>("CONVERSATION", Conversation.class);
    public static final Scope<URI> COMPOSITE = new Scope<URI>("COMPOSITE", URI.class);

    private final Class<T> identifierType;
    private final String scope;

    public Scope(String scope, Class<T> identifierType) {
        assert scope != null && identifierType != null;
        this.scope = scope.toUpperCase().intern();
        this.identifierType = identifierType;
    }

    public String getScope() {
        return scope;
    }

    public Class<T> getIdentifierType() {
        return identifierType;
    }

    @SuppressWarnings({"StringEquality"})
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Scope scope1 = (Scope) o;
        return scope == scope1.scope;
    }

    public int hashCode() {
        return scope.hashCode();
    }

    public String toString() {
        return scope;
    }
}
