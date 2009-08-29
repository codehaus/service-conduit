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
package org.sca4j.scdl.definitions;

import java.util.Set;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * Model object that represents a policy set.
 *
 * @version $Revision$ $Date$
 */
public final class PolicySet extends AbstractDefinition {
    private static final long serialVersionUID = -4507145141780962741L;

    /** Intents provided by this policy set. */
    private final Set<QName> provides;

    /** Policy set extension */
    private final Element extension;

    /**
     * XPath expression for the apples to attribute.
     */
    private final String appliesTo;

    /**
     * The phase at which the policy is applied.
     */
    private final PolicyPhase phase;

    /**
     * Initializes the state for the policy set.
     *
     * @param name      Name of the policy set.
     * @param provides  Intents provided by this policy set.
     * @param appliesTo XPath expression for the applies to attribute.
     * @param extension Extension for the policy set.
     * @param phase     The phase at which the policy is applied.
     */
    public PolicySet(QName name, Set<QName> provides, String appliesTo, Element extension, PolicyPhase phase) {
        super(name);
        this.provides = provides;
        this.appliesTo = "".equals(appliesTo) ? null : appliesTo;
        this.extension = extension;
        this.phase = phase;
    }

    /**
     * XPath expression to the element to which the policy set applies.
     *
     * @return The apples to XPath expression.
     */
    public String getAppliesTo() {
        return appliesTo;
    }

    /**
     * Checks whether the specified intent is provided by this policy set.
     *
     * @param intent Intent that needs to be checked.
     * @return True if this policy set provides to the specified intent.
     */
    public boolean doesProvide(QName intent) {
        return provides.contains(intent);
    }

    /**
     * Checks whether the specified intents is provided by this policy set.
     *
     * @param intents Intents that need to be checked.
     * @return True if this policy set provides to the specified intent.
     */
    public boolean doesProvide(Set<QName> intents) {
        return provides.containsAll(intents);
    }

    /**
     * @return Extension for the policy set.
     */
    public Element getExtension() {
        return extension;
    }

    /**
     * @return Qualified name of the extension element.
     */
    public QName getExtensionName() {
        return new QName(extension.getNamespaceURI(), extension.getLocalName());
    }

    /**
     * @return Gets the policy phase.
     */
    public PolicyPhase getPhase() {
        return phase;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PolicySet policySet = (PolicySet) o;

        if (getName() != null ? !getName().equals(policySet.getName()) : policySet.getName() != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (provides != null ? provides.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (appliesTo != null ? appliesTo.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        return result;
    }
}
