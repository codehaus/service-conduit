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
import java.util.Set;

/**
 * A directed graph.
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public interface DirectedGraph<T> {

    /**
     * Adds a vertex.
     *
     * @param vertex the vertex to add
     */
    public void add(Vertex<T> vertex);

    /**
     * Removes a vertex. Also removes any associated edges.
     *
     * @param vertex the vertex to remove
     */
    public void remove(Vertex<T> vertex);

    /**
     * Returns the vertices in the graph.
     *
     * @return the vertices in the graph
     */
    public Set<Vertex<T>> getVertices();

    /**
     * Returns the adjacent vertices to the given vertex.
     *
     * @param vertex the vertex to return adjacent vertices for
     * @return the adjacent vertices to the given vertex
     */
    public Set<Vertex<T>> getAdjacentVertices(Vertex<T> vertex);

    /**
     * Returns the adjacent vertices pointed to by outgoing edges for a given vertex.
     *
     * @param vertex the vertex
     * @return the adjacent vertices
     */
    public List<Vertex<T>> getOutgoingAdjacentVertices(Vertex<T> vertex);

    /**
     * Returns the adjacent vertices pointed to by incoming edges for a given vertex.
     *
     * @param vertex the vertex
     * @return the adjacent vertices
     */
    public List<Vertex<T>> getIncomingAdjacentVertices(Vertex<T> vertex);


    /**
     * Adds an edge.
     *
     * @param edge the edge to add
     */
    public void add(Edge<T> edge);

    /**
     * Removes an edge.
     *
     * @param edge the edge to remove
     */
    public void remove(Edge<T> edge);

    /**
     * Returns all edges in the graph.
     *
     * @return all edges in the graph
     */
    public Set<Edge<T>> getEdges();

    /**
     * Returns an edge between the two given vertices.
     *
     * @param source the source vertex
     * @param sink   the sink vertex
     * @return the edge
     */
    public Edge<T> getEdge(Vertex<T> source, Vertex<T> sink);

    /**
     * Returns the outgoing edges for the given vertex
     *
     * @param vertex the vertex to return the outgoing edges for
     * @return the outgoing edges
     */
    public Set<Edge<T>> getOutgoingEdges(Vertex<T> vertex);

    /**
     * Returns the incoming edges for the given vertex
     *
     * @param vertex the vertex to return the incoming edges for
     * @return the outgoing edges
     */
    public Set<Edge<T>> getIncomingEdges(Vertex<T> vertex);

}

