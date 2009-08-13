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

/**
 * Walks the graph until the specified vertex is reached.
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */

public class TerminatingVisitor<T> implements Visitor<T> {
    private Vertex<T> stopVertex;
    private boolean found;

    /**
     * Constructor.
     *
     * @param vertex vertex to stop at
     */
    public TerminatingVisitor(Vertex<T> vertex) {
        this.stopVertex = vertex;
    }

    /**
     * Returns true if the specified vertex was reached during the traversal.
     *
     * @return true if the specified vertex was reached during the traversal
     */
    public boolean wasFound() {
        return found;
    }

    public boolean visit(Vertex<T> vertex) {
        if (vertex == stopVertex) {
            found = true;
            return false;
        } else {
            return true;
        }
    }

}
