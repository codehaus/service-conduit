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
package org.sca4j.fabric.util.graph;

import java.util.List;

/**
 * Represents a cycle in a directed graph
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public class Cycle<T> {
    private List<Vertex<T>> originPath;
    private List<Vertex<T>> backPath;

    /**
     * Returns the list of vertices from the cycle origin to the endpoint.
     *
     * @return the list of vertices from the cycle origin to the endpoint
     */
    public List<Vertex<T>> getOriginPath() {
        return originPath;
    }

    /**
     * Sets the list of vertices from the cycle origin to the endpoint.
     *
     * @param originPath the list of vertices from the cycle origin to the endpoint
     */
    public void setOriginPath(List<Vertex<T>> originPath) {
        this.originPath = originPath;
    }

    /**
     * Returns the list of vertices from the cycle endpoint back to the origin.
     *
     * @return the the list of vertices from the cycle endpoint back to the origin
     */
    public List<Vertex<T>> getBackPath() {
        return backPath;
    }

    /**
     * Sets the list of vertices from the cycle endpoint back to the origin.
     *
     * @param backPath the the list of vertices from the cycle endpoint back to the origin
     */
    public void setBackPath(List<Vertex<T>> backPath) {
        this.backPath = backPath;
    }
}
