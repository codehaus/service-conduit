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
import java.util.List;

import javax.xml.namespace.QName;

import org.osoa.sca.Constants;

/**
 * Representation of a wire from a reference to a service.
 *
 * @version $Rev: 59 $ $Date: 2007-05-19 08:21:09 +0100 (Sat, 19 May 2007) $
 */
public final class LogicalWire extends LogicalScaArtifact<LogicalComponent<?>> {
    private static final long serialVersionUID = -643283191171197255L;

    private static final QName TYPE = new QName(Constants.SCA_NS, "wire");

    private final LogicalReference source;
    private final URI targetUri;
    private boolean provisioned;

    /**
     * Instantiates a logical wire.
     *
     * @param parent    component within which the wire is defined.
     * @param source    the source reference of the wire
     * @param targetUri the uri of the target service
     */
    public LogicalWire(LogicalComponent<?> parent, LogicalReference source, URI targetUri) {
        super(null, parent, TYPE);
        assert source != null;
        assert targetUri != null;
        this.source = source;
        this.targetUri = targetUri;
    }

    /**
     * Gets the source of the wire.
     *
     * @return Source of the wire.
     */
    public final LogicalReference getSource() {
        return source;
    }

    /**
     * Gets the target URI of the wire.
     *
     * @return Target URI of the wire.
     */
    public final URI getTargetUri() {
        return targetUri;
    }

    /**
     * Intents are not supported on wires.
     *
     * @return Intents declared on the SCA artifact.
     */
    @Override
    public final List<QName> getIntents() {
        throw new UnsupportedOperationException("Intents are not supported on wires");
    }

    /**
     * Policy sets are not supported on wires.
     */
    @Override
    public final List<QName> getPolicySets() {
        throw new UnsupportedOperationException("Policy sets are not supported on wires");
    }

    /**
     * Intents are not supported on wires.
     */
    @Override
    public final void setIntents(List<QName> intents) {
        throw new UnsupportedOperationException("Intents are not supported on wires");
    }

    /**
     * Policy sets are not supported on wires.
     *
     * @param policySets Policy sets declared on the SCA artifact.
     */
    @Override
    public final void setPolicySets(List<QName> policySets) {
        throw new UnsupportedOperationException("Policy sets are not supported on wires");
    }

    /**
     * Tests for quality whether the source and target URIs are the same.
     *
     * @param obj Object to be tested against.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        LogicalWire test = (LogicalWire) obj;
        return targetUri.equals(test.targetUri) && source.equals(test.source);

    }

    /**
     * Hashcode based on the source and target URIs.
     *
     * @return Hashcode based on the source and target URIs.
     */
    public int hashCode() {

        int hash = 7;
        hash = 31 * hash + source.hashCode();
        hash = 31 * hash + targetUri.hashCode();
        return hash;

    }

    /**
     * Checks whether the wire has been provisioned.
     *
     * @return True if the wire has been provisioned.
     */
    public boolean isProvisioned() {
        return provisioned;
    }

    /**
     * Marks thw wire as provisioned/unprovisioned.
     *
     * @param provisioned True if the wire has been provisioned.
     */
    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

}
