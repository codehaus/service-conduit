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
 * Default vertex implementation
 *
 * @version $Rev: 2079 $ $Date: 2007-11-19 16:15:17 +0000 (Mon, 19 Nov 2007) $
 */
public class VertexImpl<T> implements Vertex<T> {
    private T entity;

    /**
     * Constructor.
     *
     * @param entity entity that the vertex represents
     */
    public VertexImpl(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }


    public boolean equals(Object obj) {
        return obj instanceof Vertex && getEntity().equals(((Vertex) obj).getEntity());
    }
}
