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
package org.sca4j.fabric.policy.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.fabric.policy.infoset.PolicySetEvaluator;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.definitions.ImplementationType;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.policy.PolicyResolutionException;
import org.sca4j.spi.services.definitions.DefinitionsRegistry;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class ImplementationPolicyHelperImpl extends AbstractPolicyHelper implements ImplementationPolicyHelper {

    public ImplementationPolicyHelperImpl(@Reference DefinitionsRegistry definitionsRegistry,
                                          @Reference PolicySetEvaluator policySetEvaluator) {
        super(definitionsRegistry, policySetEvaluator);
    }
    
    public List<Intent> getProvidedIntents(LogicalComponent<?> logicalComponent, Operation<?> operation) throws PolicyResolutionException {
        
        Implementation<?> implementation = logicalComponent.getDefinition().getImplementation();
        QName type = implementation.getType();
        ImplementationType implementationType = definitionsRegistry.getDefinition(type, ImplementationType.class);
        
        // FIXME This should not happen, all implementation types should be registsred
        if(implementationType == null) {
            return Collections.emptyList();
        }
        
        List<QName> mayProvidedIntents = implementationType.getMayProvide();

        List<Intent> requiredIntents = getRequestedIntents(logicalComponent, operation);
        List<Intent> intentsToBeProvided = new LinkedList<Intent>();
        for(Intent intent : requiredIntents) {
            if(mayProvidedIntents.contains(intent.getName())) {
                intentsToBeProvided.add(intent);
            }
        }
        return intentsToBeProvided;
        
    }
    
    public List<PolicySet> resolveIntents(LogicalComponent<?> logicalComponent, Operation<?> operation, Element target) throws PolicyResolutionException {
        
        Implementation<?> implementation = logicalComponent.getDefinition().getImplementation();
        QName type = implementation.getType();
        ImplementationType implementationType = definitionsRegistry.getDefinition(type, ImplementationType.class);
        
        List<QName> alwaysProvidedIntents = new ArrayList<QName>();
        List<QName> mayProvidedIntents = new ArrayList<QName>();

        // FIXME This should not happen, all implementation types should be registsred
        if(implementationType != null) {
            alwaysProvidedIntents = implementationType.getAlwaysProvide();
            mayProvidedIntents = implementationType.getMayProvide();
        }

        List<Intent> requiredIntents = getRequestedIntents(logicalComponent, operation);
        List<Intent> requiredIntentsCopy = new LinkedList<Intent>(requiredIntents);
        List<QName> requestedPolicies = getRequestedPolicies(logicalComponent, operation);
        
        // Remove intents that are provided
        for(Intent intent : requiredIntentsCopy) {
            QName intentName = intent.getName();
            if(alwaysProvidedIntents.contains(intentName) || mayProvidedIntents.contains(intentName)) {
                requiredIntents.remove(intent);
            }
        }
        
        List<PolicySet> policies = resolvePolicies(requiredIntents, requestedPolicies, target, operation.getName());        
        if(requiredIntents.size() > 0) {
            throw new PolicyResolutionException("Unable to resolve all intents", requiredIntents);
        }
        
        return policies;
        
    }

    private List<Intent> getRequestedIntents(LogicalComponent<?> logicalComponent, Operation<?> operation) throws PolicyResolutionException {
        
        // Aggregate all the intents from the ancestors
        List<QName> intentNames = new LinkedList<QName>();
        intentNames.addAll(operation.getIntents());
        intentNames.addAll(logicalComponent.getDefinition().getImplementation().getIntents());
        intentNames.addAll(aggregateIntents(logicalComponent));
        
        // Expand all the profile intents
        List<Intent> requiredIntents = resolveProfileIntents(intentNames);
        
        // Remove intents not applicable to the artifact
        filterInvalidIntents(Intent.IMPLEMENTATION, requiredIntents);
        
        return requiredIntents;
        
    }

    private List<QName> getRequestedPolicies(LogicalComponent<?> logicalComponent, Operation<?> operation) throws PolicyResolutionException {
        
        // Aggregate all the intents from the ancestors
        List<QName> policies = new LinkedList<QName>();
        policies.addAll(operation.getPolicySets());
        policies.addAll(logicalComponent.getDefinition().getImplementation().getPolicySets());
        policies.addAll(aggregatePolicies(logicalComponent));
        
        return policies;
        
    }

}
