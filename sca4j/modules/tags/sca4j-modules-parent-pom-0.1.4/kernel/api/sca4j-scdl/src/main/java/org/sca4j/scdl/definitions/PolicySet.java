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
