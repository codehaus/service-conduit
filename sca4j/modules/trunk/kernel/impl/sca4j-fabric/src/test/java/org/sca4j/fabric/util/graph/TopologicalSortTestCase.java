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

import java.util.List;

import junit.framework.TestCase;

/**
 * @version $Rev: 2080 $ $Date: 2007-11-19 16:16:02 +0000 (Mon, 19 Nov 2007) $
 */
public class TopologicalSortTestCase extends TestCase {

    public void testMultiLevelSort() throws Exception {
        DirectedGraph<String> graph = new DirectedGraphImpl<String>();
        Vertex<String> a = new VertexImpl<String>("A");
        Vertex<String> b = new VertexImpl<String>("B");
        Edge<String> edgeAB = new EdgeImpl<String>(a, b);
        graph.add(edgeAB);
        Vertex<String> c = new VertexImpl<String>("C");
        Edge<String> edgeAC = new EdgeImpl<String>(a, c);
        graph.add(edgeAC);
        Edge<String> edgeBC = new EdgeImpl<String>(b, c);
        graph.add(edgeBC);
        List<Vertex<String>> list = new TopologicalSorterImpl<String>().sort(graph);
        assertEquals(a, list.get(0));
        assertEquals(b, list.get(1));
        assertEquals(c, list.get(2));
    }

    public void testMultiLevelReverseSort() throws Exception {
        DirectedGraph<String> graph = new DirectedGraphImpl<String>();
        Vertex<String> a = new VertexImpl<String>("A");
        Vertex<String> b = new VertexImpl<String>("B");
        Edge<String> edgeAB = new EdgeImpl<String>(a, b);
        graph.add(edgeAB);
        Vertex<String> c = new VertexImpl<String>("C");
        Edge<String> edgeAC = new EdgeImpl<String>(a, c);
        graph.add(edgeAC);
        Edge<String> edgeBC = new EdgeImpl<String>(b, c);
        graph.add(edgeBC);
        List<Vertex<String>> list = new TopologicalSorterImpl<String>().reverseSort(graph);
        assertEquals(c, list.get(0));
        assertEquals(b, list.get(1));
        assertEquals(a, list.get(2));
    }

    public void testReverseSort() throws Exception {
        DirectedGraph<String> graph = new DirectedGraphImpl<String>();
        Vertex<String> a = new VertexImpl<String>("A");
        Vertex<String> b = new VertexImpl<String>("B");
        Edge<String> edgeAB = new EdgeImpl<String>(a, b);
        graph.add(edgeAB);
        Vertex<String> c = new VertexImpl<String>("C");
        Edge<String> edgeAC = new EdgeImpl<String>(a, c);
        graph.add(edgeAC);
        List<Vertex<String>> list = new TopologicalSorterImpl<String>().reverseSort(graph);
        assertEquals(a, list.get(2));
        assertTrue(list.contains(c));
        assertTrue(list.contains(b));
    }

    public void testSort() throws Exception {
        DirectedGraph<String> graph = new DirectedGraphImpl<String>();
        Vertex<String> a = new VertexImpl<String>("A");
        Vertex<String> b = new VertexImpl<String>("B");
        Edge<String> edgeAB = new EdgeImpl<String>(a, b);
        graph.add(edgeAB);
        Vertex<String> c = new VertexImpl<String>("C");
        Edge<String> edgeAC = new EdgeImpl<String>(a, c);
        graph.add(edgeAC);
        List<Vertex<String>> list = new TopologicalSorterImpl<String>().sort(graph);
        assertEquals(a, list.get(0));
        assertTrue(list.contains(b));
        assertTrue(list.contains(c));
    }

}
