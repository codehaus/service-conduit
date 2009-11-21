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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of a topological sorter.
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public class TopologicalSorterImpl<T> implements TopologicalSorter<T> {

    public List<Vertex<T>> sort(DirectedGraph<T> dag) throws CycleException {
        // perform the sort over the entire graph, calculating roots and references for all children
        Map<Vertex<T>, AtomicInteger> vertexMap = new HashMap<Vertex<T>, AtomicInteger>();
        List<Vertex<T>> roots = new ArrayList<Vertex<T>>();
        // first pass over the graph to collect vertex references and root vertices
        Set<Vertex<T>> vertices = dag.getVertices();
        for (Vertex<T> v : vertices) {
            int incoming = dag.getIncomingEdges(v).size();
            if (incoming == 0) {
                roots.add(v);
            } else {
                AtomicInteger count = new AtomicInteger();
                count.set(incoming);
                vertexMap.put(v, count);
            }
        }
        // perform the sort
        return sort(dag, vertexMap, roots);
    }


    public List<Vertex<T>> sort(DirectedGraph<T> dag, Vertex<T> start) throws CycleException {
        // perform the sort over the subgraph graph formed from the given vertex, calculating roots and references
        // for its children
        DepthFirstTraverser<T> dfs = new DepthFirstTraverserImpl<T>();
        Map<Vertex<T>, AtomicInteger> vertexMap = new HashMap<Vertex<T>, AtomicInteger>();
        List<Vertex<T>> vertices = dfs.traverse(dag, start);
        for (Vertex<T> v : vertices) {
            List<Vertex<T>> outgoing = dag.getOutgoingAdjacentVertices(v);
            for (Vertex<T> child : outgoing) {
                AtomicInteger count = vertexMap.get(child);
                if (count == null) {
                    count = new AtomicInteger();
                    vertexMap.put(child, count);
                }
                count.incrementAndGet();
            }
        }

        List<Vertex<T>> roots = new ArrayList<Vertex<T>>();
        roots.add(start);
        // perform the sort
        return sort(dag, vertexMap, roots);
    }

    public List<Vertex<T>> reverseSort(DirectedGraph<T> dag) throws CycleException {
        List<Vertex<T>> sortSequence = sort(dag);
        Collections.reverse(sortSequence);
        return sortSequence;
    }

    public List<Vertex<T>> reverseSort(DirectedGraph<T> dag, Vertex<T> start) throws CycleException {
        List<Vertex<T>> sorted = sort(dag, start);
        Collections.reverse(sorted);
        return sorted;
    }

    /**
     * Performs the sort.
     *
     * @param dag      the DAG to sort
     * @param vertices map of vertices and references
     * @param roots    roots in the graph
     * @return the total ordering calculated by the topological sort
     * @throws CycleException if a cycle is detected
     */
    private List<Vertex<T>> sort(DirectedGraph<T> dag, Map<Vertex<T>, AtomicInteger> vertices, List<Vertex<T>> roots)
            throws CycleException {
        List<Vertex<T>> visited = new ArrayList<Vertex<T>>();
        int num = vertices.size() + roots.size();
        while (!roots.isEmpty()) {
            Vertex<T> v = roots.remove(roots.size() - 1);
            visited.add(v);
            List<Vertex<T>> outgoing = dag.getOutgoingAdjacentVertices(v);
            for (Vertex<T> child : outgoing) {
                AtomicInteger count = vertices.get(child);
                if (count.decrementAndGet() == 0) {
                    // add child to root list as all parents are processed
                    roots.add(child);
                }
            }
        }
        if (visited.size() != num) {
            throw new CycleException();
        }
        return visited;
    }

}
