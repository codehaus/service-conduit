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
package org.sca4j.fabric.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default directed graph implementation.
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class DirectedGraphImpl<T> implements DirectedGraph<T> {
    private Map<Vertex<T>, VertexHolder> graphVertices;
    private Set<Edge<T>> graphEdges;

    public DirectedGraphImpl() {
        graphVertices = new HashMap<Vertex<T>, VertexHolder>();
        graphEdges = new HashSet<Edge<T>>();
    }

    public Set<Vertex<T>> getVertices() {
        return graphVertices.keySet();
    }

    public void add(Vertex<T> vertex) {
        if (graphVertices.containsKey(vertex)) {
            return;
        }
        graphVertices.put(vertex, new VertexHolder());
    }

    public void remove(Vertex<T> vertex) {
        List<Edge<T>> vedges = new ArrayList<Edge<T>>(getOutgoingEdges(vertex));
        for (Edge<T> edge : vedges) {
            removeEdge(edge);
        }
        graphVertices.remove(vertex);
    }

    public Set<Vertex<T>> getAdjacentVertices(Vertex<T> vertex) {
        Set<Vertex<T>> adjacentVertices = new HashSet<Vertex<T>>();
        Set<Edge<T>> incidentEdges = getOutgoingEdges(vertex);
        if (incidentEdges != null) {
            for (Edge<T> edge : incidentEdges) {
                adjacentVertices.add(edge.getOppositeVertex(vertex));
            }
        }
        return adjacentVertices;
    }

    public List<Vertex<T>> getOutgoingAdjacentVertices(Vertex<T> vertex) {
        return getAdjacentVertices(vertex, true);
    }

    public List<Vertex<T>> getIncomingAdjacentVertices(Vertex<T> vertex) {
        return getAdjacentVertices(vertex, false);
    }

    public Edge<T> getEdge(Vertex<T> source, Vertex<T> sink) {
        Set<Edge<T>> edges = getOutgoingEdges(source);
        for (Edge<T> edge : edges) {
            if (edge.getSink() == sink) {
                return edge;
            }
        }
        return null;
    }

    public Set<Edge<T>> getOutgoingEdges(Vertex<T> vertex) {
        return graphVertices.get(vertex).getOutgoingEdges();
    }

    public Set<Edge<T>> getIncomingEdges(Vertex<T> vertex) {
        return graphVertices.get(vertex).getIncomingEdges();
    }

    public Set<Edge<T>> getEdges() {
        return graphEdges;
    }

    public void add(Edge<T> edge) {
        if (graphEdges.contains(edge)) {
            return;
        }
        Vertex<T> source = edge.getSource();
        Vertex<T> sink = edge.getSink();

        if (!graphVertices.containsKey(source)) {
            add(source);
        }
        if ((sink != source) && !graphVertices.containsKey(sink)) {
            add(sink);
        }
        Set<Edge<T>> sourceEdges = getOutgoingEdges(source);

        sourceEdges.add(edge);
        if (source != sink) {
            // avoid adding the edge a second time if the edge points back on itself
            Set<Edge<T>> sinkEdges = getIncomingEdges(sink);
            sinkEdges.add(edge);
        }

        graphEdges.add(edge);
        VertexHolder sourceHolder = graphVertices.get(edge.getSource());
        VertexHolder sinkHolder = graphVertices.get(edge.getSink());
        sourceHolder.getOutgoingEdges().add(edge);
        sinkHolder.getIncomingEdges().add(edge);
    }

    public void remove(Edge<T> edge) {
        removeEdge(edge);
        Vertex<T> source = edge.getSource();
        Vertex<T> sink = edge.getSink();
        VertexHolder sourceHolder = graphVertices.get(source);
        VertexHolder sinkHolder = graphVertices.get(sink);
        // remove the edge from the source's outgoing edges
        sourceHolder.getOutgoingEdges().remove(edge);
        // remove the edge from the sink's incoming edges
        sinkHolder.getIncomingEdges().remove(edge);
    }

    private void removeEdge(Edge<T> edge) {
        // Remove the edge from the vertices incident edges.
        Vertex<T> source = edge.getSource();
        Set<Edge<T>> sourceEdges = getOutgoingEdges(source);
        sourceEdges.remove(edge);

        Vertex<T> sink = edge.getSink();
        Set<Edge<T>> sinkEdges = getIncomingEdges(sink);
        sinkEdges.remove(edge);

        // Remove the edge from edgeSet
        graphEdges.remove(edge);
    }

    /**
     * Returns the outgoing or incoming adjacent vertices for a given vertex
     *
     * @param vertex   the vertex.
     * @param outGoing true for returning outgoing vertices.
     * @return the adjacent vertices
     */
    private List<Vertex<T>> getAdjacentVertices(Vertex<T> vertex, boolean outGoing) {
        List<Vertex<T>> adjacentVertices = new ArrayList<Vertex<T>>();
        Set<Edge<T>> edges;
        if (outGoing) {
            edges = getOutgoingEdges(vertex);
        } else {
            edges = getIncomingEdges(vertex);
        }
        for (Edge<T> edge : edges) {
            Vertex<T> oppositeVertex = edge.getOppositeVertex(vertex);
            adjacentVertices.add(oppositeVertex);
        }
        return adjacentVertices;
    }

    private class VertexHolder {
        private Set<Edge<T>> incoming = new HashSet<Edge<T>>();
        private Set<Edge<T>> outgoingEdges = new HashSet<Edge<T>>();

        public Set<Edge<T>> getIncomingEdges() {
            return incoming;
        }

        public Set<Edge<T>> getOutgoingEdges() {
            return outgoingEdges;
        }

    }

}

