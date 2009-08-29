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
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Detects cycles in a directed graph.
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public class CycleDetectorImpl<T> implements CycleDetector<T> {
    private DepthFirstTraverser<T> traverser;

    public CycleDetectorImpl() {
        traverser = new DepthFirstTraverserImpl<T>();
    }

    public boolean hasCycles(DirectedGraph<T> graph) {
        for (Vertex<T> vertex : graph.getVertices()) {
            if (isCycle(graph, vertex)) {
                return true;
            }
        }
        return false;
    }

    public DirectedGraph<T> findCycleSubgraph(DirectedGraph<T> graph) {
        DirectedGraph<T> subGraph = new DirectedGraphImpl<T>();
        for (Edge<T> edge : graph.getEdges()) {
            if (isPath(graph, edge.getSink(), edge.getSource())) {
                subGraph.add(edge);
            }
        }
        return subGraph;
    }

    public List<Cycle<T>> findCycles(DirectedGraph<T> graph) {
        List<Cycle<T>> cycles = new ArrayList<Cycle<T>>();
        for (Edge<T> edge : graph.getEdges()) {
            List<Vertex<T>> path = getPath(graph, edge.getSink(), edge.getSource());
            if (!path.isEmpty()) {
                Cycle<T> cycle = searchCycle(cycles, edge);
                if (cycle == null) {
                    cycle = new Cycle<T>();
                    cycle.setOriginPath(path);
                    cycles.add(cycle);
                } else {
                    cycle.setBackPath(path);
                }
            }
        }
        return cycles;
    }

    private Cycle<T> searchCycle(List<Cycle<T>> cycles, Edge<T> edge) {
        for (Cycle<T> cycle : cycles) {
            List<Vertex<T>> path = cycle.getOriginPath();
            Vertex<T> vertex = path.get(0);
            if (vertex.equals(edge.getSink())) {
                return cycle;
            }
        }
        return null;
    }

    private boolean isCycle(DirectedGraph<T> graph, Vertex<T> from) {
        Set<Edge<T>> edges = graph.getOutgoingEdges(from);
        for (Edge<T> edge : edges) {
            Vertex<T> opposite = edge.getOppositeVertex(from);
            if (isPath(graph, opposite, from)) {
                // cycle found
                return true;
            }
        }
        return false;
    }


    /**
     * Returns true if a path exists between two vertices.
     *
     * @param graph the graph to search
     * @param start the starting vertex
     * @param end   the ending vertex
     * @return true if a path exists between two vertices
     */
    private boolean isPath(DirectedGraph<T> graph, Vertex<T> start, Vertex<T> end) {
        return !getPath(graph, start, end).isEmpty();
    }

    /**
     * Returns the ordered list of vertices traversed for a path defined by the given start and end vertices. If no path
     * exists, an empty colleciton is returned.
     *
     * @param graph the graph to search
     * @param start the starting vertex
     * @param end   the ending vertex
     * @return the ordered collection of vertices or an empty collection if no path exists
     */
    private List<Vertex<T>> getPath(DirectedGraph<T> graph, Vertex<T> start, Vertex<T> end) {
        List<Vertex<T>> path = traverser.traversePath(graph, start, end);
        // reverse the order to it goes from source to end destination
        Collections.reverse(path);
        return path;
    }

}
