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

import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Rev: 2080 $ $Date: 2007-11-19 16:16:02 +0000 (Mon, 19 Nov 2007) $
 */
public class CycleTestCase extends TestCase {

    public void testCycle() throws Exception {
        DirectedGraph<String> graph = new DirectedGraphImpl<String>();
        Vertex<String> a = new VertexImpl<String>("A");
        Vertex<String> b = new VertexImpl<String>("B");
        Edge<String> edgeAB = new EdgeImpl<String>(a, b);
        graph.add(edgeAB);
        Edge<String> edgeBA = new EdgeImpl<String>(b, a);
        graph.add(edgeBA);
        CycleDetector<String> detector = new CycleDetectorImpl<String>();
        assertTrue(detector.hasCycles(graph));
        DirectedGraph<String> dg = detector.findCycleSubgraph(graph);
        Set<Edge<String>> edges = dg.getEdges();
        assertTrue(edges.contains(edgeAB));
        assertTrue(edges.contains(edgeBA));
    }
}
