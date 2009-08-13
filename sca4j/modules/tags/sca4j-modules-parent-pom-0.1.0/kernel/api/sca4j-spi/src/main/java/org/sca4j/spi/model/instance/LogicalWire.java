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
    public final Set<QName> getIntents() {
        throw new UnsupportedOperationException("Intents are not supported on wires");
    }

    /**
     * Policy sets are not supported on wires.
     */
    @Override
    public final Set<QName> getPolicySets() {
        throw new UnsupportedOperationException("Policy sets are not supported on wires");
    }

    /**
     * Intents are not supported on wires.
     */
    @Override
    public final void setIntents(Set<QName> intents) {
        throw new UnsupportedOperationException("Intents are not supported on wires");
    }

    /**
     * Policy sets are not supported on wires.
     *
     * @param policySets Policy sets declared on the SCA artifact.
     */
    @Override
    public final void setPolicySets(Set<QName> policySets) {
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
