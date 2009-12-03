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
package org.sca4j.jmx.runtime;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @version $Rev: 3690 $ $Date: 2008-04-22 20:06:52 +0100 (Tue, 22 Apr 2008) $
 */
public class OperationKey {
    private final String name;
    private final String[] params;
    private final int hashCode;

    public OperationKey(String name, String[] params) {
        this.name = name;
        this.params = params;
        hashCode = 31 * this.name.hashCode() + Arrays.hashCode(this.params);
    }

    public OperationKey(Method method) {
        this.name = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();
        this.params = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = paramTypes[i].getName();
        }
        hashCode = 31 * this.name.hashCode() + Arrays.hashCode(this.params);
    }

    public String toString() {
        StringBuilder sig = new StringBuilder();
        sig.append(name).append('(');
        if (params.length > 0) {
            sig.append(params[0]);
            for (int i = 1; i < params.length; i++) {
                sig.append(',').append(params[i]);
            }
        }
        sig.append(')');
        return sig.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationKey that = (OperationKey) o;

        return name.equals(that.name) && Arrays.equals(params, that.params);

    }

    public int hashCode() {
        return hashCode;
    }
}
