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

