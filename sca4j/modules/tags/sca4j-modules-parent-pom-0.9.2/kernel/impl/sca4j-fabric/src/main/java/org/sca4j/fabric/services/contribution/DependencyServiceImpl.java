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
package org.sca4j.fabric.services.contribution;

import java.util.ArrayList;
import java.util.List;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.fabric.util.graph.Cycle;
import org.sca4j.fabric.util.graph.CycleDetector;
import org.sca4j.fabric.util.graph.CycleDetectorImpl;
import org.sca4j.fabric.util.graph.DirectedGraph;
import org.sca4j.fabric.util.graph.DirectedGraphImpl;
import org.sca4j.fabric.util.graph.Edge;
import org.sca4j.fabric.util.graph.EdgeImpl;
import org.sca4j.fabric.util.graph.GraphException;
import org.sca4j.fabric.util.graph.TopologicalSorter;
import org.sca4j.fabric.util.graph.TopologicalSorterImpl;
import org.sca4j.fabric.util.graph.Vertex;
import org.sca4j.fabric.util.graph.VertexImpl;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.Export;
import org.sca4j.spi.services.contribution.Import;
import org.sca4j.spi.services.contribution.MetaDataStore;

/**
 * Default implementation of the DependencyService
 *
 * @version $Rev: 4825 $ $Date: 2008-06-13 23:21:32 +0100 (Fri, 13 Jun 2008) $
 */
public class DependencyServiceImpl implements DependencyService {
    private CycleDetector<Contribution> detector;
    private TopologicalSorter<Contribution> sorter;
    private MetaDataStore store;


    public DependencyServiceImpl(@Reference MetaDataStore store) {
        this.store = store;
        detector = new CycleDetectorImpl<Contribution>();
        sorter = new TopologicalSorterImpl<Contribution>();
    }

    public List<Contribution> order(List<Contribution> contributions) throws ContributionException {
        // create a DAG
        DirectedGraph<Contribution> dag = new DirectedGraphImpl<Contribution>();
        // add the contributions as vertices
        for (Contribution contribution : contributions) {
            dag.add(new VertexImpl<Contribution>(contribution));
        }
        // add edges based on imports
        for (Vertex<Contribution> source : dag.getVertices()) {
            Contribution contribution = source.getEntity();
            ContributionManifest manifest = contribution.getManifest();
            assert manifest != null;
            for (Import imprt : manifest.getImports()) {
                // first, see if the import is already installed
                // note that extension imports do not need to be checked since we assume extensons are installed prior
                if (store.resolve(imprt) != null) {
                    continue;
                }
                Vertex<Contribution> sink = findTargetVertex(dag, imprt);
                if (sink == null) {
                    String uri = contribution.getUri().toString();
                    throw new UnresolvableImportException("Unable to resolve import " + imprt + " in contribution " + uri, uri, imprt);
                }
                Edge<Contribution> edge = new EdgeImpl<Contribution>(source, sink);
                dag.add(edge);
            }

        }
        // detect cycles
        List<Cycle<Contribution>> cycles = detector.findCycles(dag);
        if (!cycles.isEmpty()) {
            // cycles were detected
            throw new CyclicDependencyException(cycles);
        }
        try {
            List<Vertex<Contribution>> vertices = sorter.reverseSort(dag);
            List<Contribution> ordered = new ArrayList<Contribution>(vertices.size());
            for (Vertex<Contribution> vertex : vertices) {
                ordered.add(vertex.getEntity());
            }
            return ordered;
        } catch (GraphException e) {
            throw new ContributionException(e);
        }
    }

    /**
     * Finds the Vertex in the graph with a maching export
     *
     * @param dag   the graph to resolve against
     * @param imprt the import to resolve
     * @return the matching Vertext or null
     */
    private Vertex<Contribution> findTargetVertex(DirectedGraph<Contribution> dag, Import imprt) {
        for (Vertex<Contribution> vertex : dag.getVertices()) {
            Contribution contribution = vertex.getEntity();
            ContributionManifest manifest = contribution.getManifest();
            assert manifest != null;
            for (Export export : manifest.getExports()) {
                if (Export.EXACT_MATCH == export.match(imprt)) {
                    return vertex;
                }
            }
        }
        return null;
    }

}
