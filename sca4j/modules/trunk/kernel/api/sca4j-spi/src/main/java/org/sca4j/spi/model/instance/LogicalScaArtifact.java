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
package org.sca4j.spi.model.instance;

import java.net.URI;
import java.util.Set;
import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * Super class for all logical SCA artifacts.
 *
 * @version $Revision$ $Date$
 */
public abstract class LogicalScaArtifact<P extends LogicalScaArtifact<?>> implements Serializable {
    private static final long serialVersionUID = 3937960041374196627L;
    private final URI uri;
    private final P parent;
    private final QName type;

    /**
     * @param uri URI of the SCA artifact.
     * @param parent Parent of the SCA artifact.
     * @param type Type of this artifact.
     */
    public LogicalScaArtifact(final URI uri, final P parent, final QName type) {
        this.uri = uri;
        this.parent = parent;
        this.type = type;
    }

    /**
     * Returns the uri.
     *
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return Type of this SCA artifact.
     */
    public QName getType() {
        return type;
    }
    
    /**
     * @return Parent of this SCA artifact.
     */
    public final P getParent() {
        return parent;
    }

    public String toString() {
        return uri.toString();
    }

    /**
     * @return Intents declared on the SCA artifact.
     */
    public abstract Set<QName> getIntents();
    
    /**
     * @param intents Intents declared on the SCA artifact.
     */
    public abstract void setIntents(Set<QName> intents);

    /**
     * @return Policy sets declared on the SCA artifact.
     */
    public abstract Set<QName> getPolicySets() ;

    /**
     * @param policySets Policy sets declared on the SCA artifact.
     */
    public abstract void setPolicySets(Set<QName> policySets) ;

}
