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
