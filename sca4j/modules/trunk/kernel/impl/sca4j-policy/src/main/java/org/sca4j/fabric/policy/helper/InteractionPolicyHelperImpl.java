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
package org.sca4j.fabric.policy.helper;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sca4j.fabric.policy.infoset.PolicySetEvaluator;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.definitions.BindingType;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.PolicyResolutionException;
import org.sca4j.spi.services.definitions.DefinitionsRegistry;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class InteractionPolicyHelperImpl extends AbstractPolicyHelper implements InteractionPolicyHelper {

    public InteractionPolicyHelperImpl(@Reference DefinitionsRegistry definitionsRegistry,
                                       @Reference PolicySetEvaluator policySetEvaluator) {
        super(definitionsRegistry, policySetEvaluator);
    }

    public Set<Intent> getProvidedIntents(LogicalBinding<?> logicalBinding, Operation<?> operation) throws PolicyResolutionException {
        
        QName type = logicalBinding.getBinding().getType();
        BindingType bindingType = definitionsRegistry.getDefinition(type, BindingType.class);
        
        // FIXME This should not happen, all binding types should be registsred
        if (bindingType == null) {
            return Collections.emptySet();
        }

        Set<QName> mayProvidedIntents = bindingType.getMayProvide();
        
        Set<Intent> requiredIntents = getRequestedIntents(logicalBinding, operation);
        
        Set<Intent> intentsToBeProvided = new LinkedHashSet<Intent>();
        for(Intent intent : requiredIntents) {
            if(mayProvidedIntents.contains(intent.getName())) {
                intentsToBeProvided.add(intent);
            }
        }
        
        return intentsToBeProvided;
        
    }
    
    public Set<PolicySet> resolveIntents(LogicalBinding<?> logicalBinding, Operation<?> operation, Element target) throws PolicyResolutionException {

        QName type = logicalBinding.getBinding().getType();
        BindingType bindingType = definitionsRegistry.getDefinition(type, BindingType.class);
        
        Set<QName> alwaysProvidedIntents = new LinkedHashSet<QName>();
        Set<QName> mayProvidedIntents = new LinkedHashSet<QName>();

        // FIXME This should not happen, all binding types should be registsred
        if (bindingType != null) {
            alwaysProvidedIntents = bindingType.getAlwaysProvide();
            mayProvidedIntents = bindingType.getMayProvide();
        }

        Set<Intent> requiredIntents = getRequestedIntents(logicalBinding, operation);
        Set<Intent> requiredIntentsCopy = new HashSet<Intent>(requiredIntents);
        
        // Remove intents that are provided
        for(Intent intent : requiredIntentsCopy) {
            QName intentName = intent.getName();
            if(alwaysProvidedIntents.contains(intentName) || mayProvidedIntents.contains(intentName)) {
                requiredIntents.remove(intent);
            }
        }
        
        Set<PolicySet> policies = resolvePolicies(requiredIntents, target, operation.getName());        
        if(requiredIntents.size() > 0) {
            throw new PolicyResolutionException("Unable to resolve all intents", requiredIntents);
        }
        
        return policies;
        
    }

    private Set<Intent> getRequestedIntents(LogicalBinding<?> logicalBinding, 
                                            Operation<?> operation) throws PolicyResolutionException {
        
        // Aggregate all the intents from the ancestors
        Set<QName> intentNames = new LinkedHashSet<QName>();
        intentNames.addAll(operation.getIntents());
        intentNames.addAll(logicalBinding.getBinding().getIntents());
        intentNames.addAll(aggregateIntents(logicalBinding));
        
        // Expand all the profile intents
        Set<Intent> requiredIntents = resolveProfileIntents(intentNames);
        
        // Remove intents not applicable to the artifact
        filterInvalidIntents(Intent.BINDING, requiredIntents);
        
        return requiredIntents;
        
    }

}
