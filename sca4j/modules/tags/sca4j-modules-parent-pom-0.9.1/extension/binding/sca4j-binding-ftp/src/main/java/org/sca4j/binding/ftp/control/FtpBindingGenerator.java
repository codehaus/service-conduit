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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.sca4j.binding.ftp.control;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.sca4j.binding.ftp.common.Constants;
import org.sca4j.binding.ftp.provision.FtpSecurity;
import org.sca4j.binding.ftp.provision.FtpWireSourceDefinition;
import org.sca4j.binding.ftp.provision.FtpWireTargetDefinition;
import org.sca4j.binding.ftp.scdl.FtpBindingDefinition;
import org.sca4j.binding.ftp.scdl.TransferMode;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class FtpBindingGenerator implements BindingGenerator<FtpWireSourceDefinition, FtpWireTargetDefinition, FtpBindingDefinition> {

    /**
     * {@inheritDoc}
     */
    public FtpWireSourceDefinition generateWireSource(LogicalBinding<FtpBindingDefinition> binding,
                                                      Policy policy,
                                                      ServiceDefinition serviceDefinition) throws GenerationException {

        ServiceContract serviceContract = serviceDefinition.getServiceContract();
        if (serviceContract.getOperations().size() <= 0 || serviceContract.getOperations().size() > 2) {
            throw new AssertionError("Expects one download and/or an upload method");
        }

        URI id = binding.getParent().getParent().getParent().getUri();
        FtpWireSourceDefinition hwsd = new FtpWireSourceDefinition();
        hwsd.setClassLoaderId(id);
        URI targetUri = binding.getBinding().getTargetUri();
        hwsd.setUri(targetUri);

        return hwsd;

    }

    /**
     * Generates the wire target by setting the:
     * <li>Target Server URI
     * <li>FTP Security Policy
     * <li>
     */
    public FtpWireTargetDefinition generateWireTarget(LogicalBinding<FtpBindingDefinition> binding,
                                                      Policy policy,
                                                      ReferenceDefinition referenceDefinition) throws GenerationException {

        ServiceContract serviceContract = referenceDefinition.getServiceContract();
        if (serviceContract.getOperations().size() <= 0 || serviceContract.getOperations().size() > 2) {
            throw new AssertionError("Expects one download and/or an upload method");
        }

        URI id = binding.getParent().getParent().getParent().getUri();
        FtpBindingDefinition definition = binding.getBinding();
        boolean active = binding.getBinding().getTransferMode() == TransferMode.ACTIVE;

        FtpSecurity security = processPolicies(policy, serviceContract.getOperations().iterator().next());

        FtpWireTargetDefinition hwtd = new FtpWireTargetDefinition(id, active, security);
        hwtd.setUri(binding.getBinding().getTargetUri());
        hwtd.setTmpFileSuffix(definition.getTmpFileSuffix());

        return hwtd;

    }

    private FtpSecurity processPolicies(Policy policy, Operation operation) throws GenerationException {

        List<PolicySet> policySets = policy.getProvidedPolicySets(operation);
        if (policySets == null || policySets.size() == 0) {
            return null;
        }
        if (policySets.size() != 1) {
            throw new GenerationException("Invalid policy configuration, only supports security policy " + policySets);
        }

        PolicySet policySet = policySets.iterator().next();

        QName policyQName = policySet.getExtensionName();
        if (!policyQName.equals(Constants.POLICY_QNAME)) {
            throw new GenerationException("Unexpected policy element " + policyQName);
        }

        Element policyElement = policySet.getExtension();
        String user = policyElement.getAttribute("user");
        if (user == null) {
            throw new GenerationException("User name not specified in security policy");
        }
        String password = policyElement.getAttribute("password");
        if (password == null) {
            throw new GenerationException("Password not specified in security policy");
        }

        return new FtpSecurity(user, password);

    }

}
