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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;

import org.osoa.sca.Constants;

import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.ReferenceDefinition;

/**
 * Represents a resolved reference
 *
 * @version $Rev: 5280 $ $Date: 2008-08-26 15:57:57 +0100 (Tue, 26 Aug 2008) $
 */
public class LogicalReference extends Bindable {
    private static final long serialVersionUID = 2308698868251298609L;

    private static final QName TYPE = new QName(Constants.SCA_NS, "reference");

    private final ReferenceDefinition definition;
    private List<URI> promotedUris;
    private boolean resolved;

    /**
     * Constructor.
     *
     * @param uri        the reference URI
     * @param definition the reference type definition
     * @param parent     the parent component
     */
    public LogicalReference(URI uri, ReferenceDefinition definition, LogicalComponent<?> parent) {
        super(uri, parent, TYPE);
        this.definition = definition;
        promotedUris = new ArrayList<URI>();
    }

    /**
     * Returns the reference type definition.
     *
     * @return the reference type definition
     */
    public ReferenceDefinition getDefinition() {
        return definition;
    }

    /**
     * Returns the wires for the reference.
     *
     * @return the wires for the reference
     */
    public Set<LogicalWire> getWires() {
        return getComposite().getWires(this);
    }

    /**
     * Returns the URIs of component references promoted by this reference.
     *
     * @return the URIs
     */
    public List<URI> getPromotedUris() {
        return promotedUris;
    }

    /**
     * Adds the URI of a component reference promoted by this reference.
     *
     * @param uri the promoted URI
     */
    public void addPromotedUri(URI uri) {
        promotedUris.add(uri);
    }

    /**
     * Sets the  URI of the reference promoted by this reference at the given index
     *
     * @param index the index
     * @param uri   the  URI
     */
    public void setPromotedUri(int index, URI uri) {
        promotedUris.set(index, uri);
    }

    /**
     * Returns true if this reference's target (or targets) has been resolved.
     *
     * @return true if this reference's target (or targets) has been resolved
     */
    public boolean isResolved() {
        return resolved;
    }

    /**
     * Sets if this reference's target (or targets) has been resolved.
     *
     * @param resolved true if resolved.
     */
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    /**
     * Returns the intents declared on the SCA artifact.
     *
     * @return the intents declared on the SCA artifact
     */
    public List<QName> getIntents() {
        return definition.getIntents();
    }

    /**
     * Sets the intents declared on the SCA artifact.
     *
     * @param intents Intents declared on the SCA artifact
     */
    public void setIntents(List<QName> intents) {
        definition.setIntents(intents);
    }

    /**
     * Adds intents to the definition.
     *
     * @param intents the intents
     */
    public void addIntents(List<QName> intents) {
        definition.addIntents(intents);
    }

    /**
     * Returns policy sets declared on the SCA artifact.
     *
     * @return policy sets declared on the SCA artifact
     */
    public List<QName> getPolicySets() {
        return definition.getPolicySets();
    }

    /**
     * Sets policy sets declared on the SCA artifact.
     *
     * @param policySets the policy sets
     */
    public void setPolicySets(List<QName> policySets) {
        definition.setPolicySets(policySets);
    }

    /**
     * Sets policy sets declared on the SCA artifact.
     *
     * @param policySets the policy sets.
     */
    public void addPolicySets(List<QName> policySets) {
        definition.addPolicySets(policySets);
    }

    /**
     * Gets the explicit referenceassociated with this logical reference.
     *
     * @return Component reference if defined, otherwise null.
     */
    public ComponentReference getComponentReference() {
        return getParent().getDefinition().getReferences().get(getDefinition().getName());
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        LogicalReference test = (LogicalReference) obj;
        return getUri().equals(test.getUri());

    }

    @Override
    public int hashCode() {
        return getUri().hashCode();
    }


    private LogicalCompositeComponent getComposite() {

        LogicalComponent<?> parent = getParent();
        LogicalCompositeComponent composite = parent.getParent();

        return composite != null ? composite : (LogicalCompositeComponent) parent;

    }

}
