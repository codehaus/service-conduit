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
