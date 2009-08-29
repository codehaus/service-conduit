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
package org.sca4j.scdl;

/**
 * Enumeration for multiplicity.
 */
public enum Multiplicity {
    /**
     * Indicates a relationship that is optionally connected to the requestor and which, if supplied, must be connected
     * to exactly one provider.
     */
    ZERO_ONE("0..1"),

    /**
     * Indicates a relationship that must be connected between exactly one requestor and exactly one provider.
     */
    ONE_ONE("1..1"),

    /**
     * Indicates a relationship that is optionally connects the requestor to zero to unbounded providers.
     */
    ZERO_N("0..n"),

    /**
     * Indicates a relationship that must be connected at the requestor and which connects it to zero to unbounded
     * providers.
     */
    ONE_N("1..n");

    private final String text;

    Multiplicity(String value) {
        this.text = value;
    }

    /**
     * Returns the textual form of Multiplicity as defined by the Assembly spec.
     *
     * @return the textual form of Multiplicity as defined by the Assembly spec
     */
    public String toString() {
        return text;
    }

    /**
     * Parse the text form as defined by the Assembly spec.
     *
     * @param text multiplicity value as text as described by the Assembly spec; may be null
     * @return the value corresponding to the text, or null if text is null
     * @throws IllegalArgumentException if the text is not a valid value
     */
    public static Multiplicity fromString(String text) throws IllegalArgumentException {
        if (text == null) {
            return null;
        }

        for (Multiplicity multiplicity : Multiplicity.values()) {
            if (multiplicity.text.equals(text)) {
                return multiplicity;
            }
        }
        throw new IllegalArgumentException(text);
    }
}
