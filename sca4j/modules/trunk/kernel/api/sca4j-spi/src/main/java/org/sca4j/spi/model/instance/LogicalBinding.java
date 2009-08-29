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
