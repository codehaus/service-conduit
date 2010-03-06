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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.sca4j.fabric.policy.infoset.PolicySetEvaluator;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.model.instance.LogicalScaArtifact;
import org.sca4j.spi.policy.PolicyResolutionException;
import org.sca4j.spi.services.definitions.DefinitionsRegistry;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class AbstractPolicyHelper {
    
    private final PolicySetEvaluator policySetEvaluator;
    protected final DefinitionsRegistry definitionsRegistry;
    
    protected AbstractPolicyHelper(DefinitionsRegistry definitionsRegistry, PolicySetEvaluator policySetEvaluator) {
        this.definitionsRegistry = definitionsRegistry;
        this.policySetEvaluator = policySetEvaluator;
    }

    /*
     * Resolve the policies.
     */
    protected List<PolicySet> resolvePolicies(List<Intent> requiredIntents, List<QName> requestedPolicies, Element target, String operation) throws PolicyResolutionException {

        List<PolicySet> policies = new LinkedList<PolicySet>();

        Iterator<Intent> iterator = requiredIntents.iterator();
        while(iterator.hasNext()) {
            Intent intent = iterator.next();
            for (PolicySet policySet : definitionsRegistry.getAllDefinitions(PolicySet.class)) {
                if(policySet.doesProvide(intent.getName())) {
                    String appliesTo = policySet.getAppliesTo();
                    if (appliesTo == null || policySetEvaluator.doesApply(target, appliesTo, operation)) {
                        if (!policies.contains(policySet)) {
                            policies.add(policySet);
                        }
                        iterator.remove();
                    }
                }
            }
        }
        
        for (QName policy : requestedPolicies) {
            PolicySet policySet = definitionsRegistry.getDefinition(policy, PolicySet.class);
            if (policySet == null) {
                throw new PolicyResolutionException("Unregistered policy set requested", policy);
            }
            policies.add(policySet);
        }
        
        return policies;

    }

    /*
     * Filter invalid intents.
     */
    protected void filterInvalidIntents(QName type, List<Intent> requiredIntents) throws PolicyResolutionException {

        for (Iterator<Intent> it = requiredIntents.iterator();it.hasNext();) {
            
            Intent intent = it.next();

            QName intentName = intent.getName();

            if (intent.getIntentType() != null) {
                if (!intent.doesConstrain(type)) {
                    it.remove();
                }
            } else {
                if (!intent.isQualified()) {
                    throw new PolicyResolutionException("Unqualified intent without constrained artifact", intentName);
                }
                Intent qualifiableIntent = definitionsRegistry.getDefinition(intent.getQualifiable(), Intent.class);
                if (qualifiableIntent == null) {
                    throw new PolicyResolutionException("Unknown intent", intent.getQualifiable());
                }
                if (!qualifiableIntent.doesConstrain(type)) {
                    it.remove();
                }
            }
        }

    }

    /*
     * Aggregate intents from ancestors.
     */
    protected List<QName> aggregateIntents(final LogicalScaArtifact<?> scaArtifact) {

        LogicalScaArtifact<?> temp = scaArtifact;

        List<QName> intentNames = new LinkedList<QName>();
        while (temp != null) {
            intentNames.addAll(temp.getIntents());
            temp = temp.getParent();
        }
        return intentNames;

    }

    /*
     * Aggregate policies from ancestors.
     */
    protected List<QName> aggregatePolicies(final LogicalScaArtifact<?> scaArtifact) {

        LogicalScaArtifact<?> temp = scaArtifact;

        List<QName> policies = new LinkedList<QName>();
        while (temp != null) {
            policies.addAll(temp.getPolicySets());
            temp = temp.getParent();
        }
        return policies;

    }

    /*
     * Expand profile intents.
     */
    protected List<Intent> resolveProfileIntents(List<QName> intentNames) throws PolicyResolutionException {

        List<Intent> requiredIntents = new LinkedList<Intent>();

        for (QName intentName : intentNames) {

            Intent intent = definitionsRegistry.getDefinition(intentName, Intent.class);
            if (intent == null) {
                throw new PolicyResolutionException("Unknown intent", intentName);
            }

            if (intent.isProfile()) {
                for (QName requiredInentName : intent.getRequires()) {
                    Intent requiredIntent = definitionsRegistry.getDefinition(requiredInentName, Intent.class);
                    if (requiredIntent == null) {
                        throw new PolicyResolutionException("Unknown intent", requiredInentName);
                    }
                    requiredIntents.add(requiredIntent);

                }
            } else {
                requiredIntents.add(intent);
            }

        }
        return requiredIntents;

    }

}
