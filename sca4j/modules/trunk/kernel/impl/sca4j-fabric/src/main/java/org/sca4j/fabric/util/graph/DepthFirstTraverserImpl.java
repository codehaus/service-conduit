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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Default implementation of a depth first search.
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public class DepthFirstTraverserImpl<T> implements DepthFirstTraverser<T> {

    public List<Vertex<T>> traverse(DirectedGraph<T> graph, Vertex<T> start) {
        return traverse(graph, start, new TrueVisitor<T>());
    }

    public List<Vertex<T>> traversePath(DirectedGraph<T> graph, Vertex<T> start, Vertex<T> end) {
        TerminatingVisitor<T> visitor = new TerminatingVisitor<T>(end);
        List<Vertex<T>> path = traverse(graph, start, visitor);
        if (visitor.wasFound()) {
            return path;
        }
        return Collections.emptyList();
    }

    private List<Vertex<T>> traverse(DirectedGraph<T> graph, Vertex<T> start, Visitor<T> visitor) {
        List<Vertex<T>> visited = new ArrayList<Vertex<T>>();
        List<Vertex<T>> stack = new ArrayList<Vertex<T>>();
        Set<Vertex<T>> seen = new HashSet<Vertex<T>>(visited);
        stack.add(start);
        seen.add(start);
        do {
            // mark as visited
            Vertex<T> next = stack.remove(stack.size() - 1);
            visited.add(next);
            if (!visitor.visit(next)) {
                return visited;
            }

            // add all non-visited adjacent vertices to the stack
            Set<Vertex<T>> adjacentVertices = graph.getAdjacentVertices(next);
            for (Vertex<T> v : adjacentVertices) {
                seen.add(v);
                stack.add(v);
            }

        } while (!stack.isEmpty());
        return visited;
    }


}
