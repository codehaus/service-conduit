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
 * Performs topological sorts of a directed acyclic graph (DAG).
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public interface TopologicalSorter<T> {
    /**
     * Performs a topological sort of the graph.
     *
     * @param dag the DAG to sort
     * @return the total ordered list of vertices.
     * @throws GraphException if a cycle or other error is detected
     */
    List<Vertex<T>> sort(DirectedGraph<T> dag) throws GraphException;

    /**
     * Performs a topological sort of the subgraph reachable from the outgoing edges of the given vertex.
     *
     * @param dag   the DAG to sort
     * @param start the starting vertex.
     * @return the total ordered list of vertice
     * @throws GraphException if a cycle or other error is detected
     */
    List<Vertex<T>> sort(DirectedGraph<T> dag, Vertex<T> start) throws GraphException;

    /**
     * Performs a reverse topological sort of the subgraph reachable from the outgoing edges of the given vertex.
     *
     * @param dag the DAG to sort
     * @return the sorted list of vertices.
     * @throws GraphException if a cycle or other error is detected
     */
    List<Vertex<T>> reverseSort(DirectedGraph<T> dag) throws GraphException;

    /**
     * Performs a topological sort of the subgraph reachable from the outgoing edges of the given vertex.
     *
     * @param dag   the DAG to sort
     * @param start the starting vertex.
     * @return the total ordered list of vertices
     * @throws GraphException if a cycle or other error is detected
     */
    List<Vertex<T>> reverseSort(DirectedGraph<T> dag, Vertex<T> start) throws GraphException;
}
