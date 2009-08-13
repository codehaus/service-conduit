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

import java.util.Set;

import javax.xml.namespace.QName;

import org.sca4j.scdl.BindingDefinition;
import org.osoa.sca.Constants;

/**
 * Represents a resolved binding
 *
 * @version $Rev: 5280 $ $Date: 2008-08-26 15:57:57 +0100 (Tue, 26 Aug 2008) $
 */
public class LogicalBinding<BD extends BindingDefinition> extends LogicalScaArtifact<Bindable> {
    private static final long serialVersionUID = 8153501808553226042L;

    private static final QName TYPE = new QName(Constants.SCA_NS, "binding");
    
    private final BD binding;
    private boolean provisioned;

    /**
     * @param binding
     * @param parent
     */
    public LogicalBinding(BD binding, Bindable parent) {
        super(null, parent, TYPE);
        this.binding = binding;
    }

    /**
     * Returns the binding definition.
     *
     * @return the binding definition
     */
    public BD getBinding() {
        return binding;
    }

    /**
     * @return Intents declared on the SCA artifact.
     */
    public Set<QName> getIntents() {
        return binding.getIntents();
    }
    
    /**
     * @param intents Intents declared on the SCA artifact.
     */
    public void setIntents(Set<QName> intents) {
        binding.setIntents(intents);
    }

    /**
     * @return Policy sets declared on the SCA artifact.
     */
    public Set<QName> getPolicySets() {
        return binding.getPolicySets();
    }

    /**
     * @param policySets Policy sets declared on the SCA artifact.
     */
    public void setPolicySets(Set<QName> policySets) {
        binding.setPolicySets(policySets);
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }
}
