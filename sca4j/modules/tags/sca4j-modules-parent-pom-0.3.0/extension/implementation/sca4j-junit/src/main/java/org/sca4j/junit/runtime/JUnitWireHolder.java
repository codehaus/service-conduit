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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.junit.runtime;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.sca4j.maven.runtime.WireHolder;
import org.sca4j.spi.wire.Wire;

public class JUnitWireHolder implements WireHolder {
    Map<String, Wire> wires = new LinkedHashMap<String, Wire>();

    public int size() {
        return wires.size();
    }

    public boolean isEmpty() {
        return wires.isEmpty();
    }

    public boolean containsKey(Object key) {
        return wires.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return wires.containsValue(value);
    }

    public Wire get(Object key) {
        return wires.get(key);
    }

    public Wire put(String key, Wire value) {
        return wires.put(key, value);
    }

    public Wire remove(Object key) {
        return wires.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Wire> t) {
        wires.putAll(t);
    }

    public void clear() {
        wires.clear();
    }

    public Set<String> keySet() {
        return wires.keySet();
    }

    public Collection<Wire> values() {
        return wires.values();
    }

    public Set<Entry<String, Wire>> entrySet() {
        return wires.entrySet();
    }
}

