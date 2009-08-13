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
package org.sca4j.fabric.policy.helper;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
    protected Set<PolicySet> resolvePolicies(Set<Intent> requiredIntents, Element target, String operation) throws PolicyResolutionException {

        Set<PolicySet> policies = new LinkedHashSet<PolicySet>();
        
        for (PolicySet policySet : definitionsRegistry.getAllDefinitions(PolicySet.class)) {
            Iterator<Intent> iterator = requiredIntents.iterator();
            while(iterator.hasNext()) {
                Intent intent = iterator.next();
                if(policySet.doesProvide(intent.getName())) {
                    String appliesTo = policySet.getAppliesTo();
                    if (appliesTo == null || policySetEvaluator.doesApply(target, appliesTo, operation)) {
                        policies.add(policySet);
                        iterator.remove();
                    }
                }
            }
        }
        
        return policies;

    }

    /*
     * Filter invalid intents.
     */
    protected void filterInvalidIntents(QName type, Set<Intent> requiredIntents) throws PolicyResolutionException {

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
    protected Set<QName> aggregateIntents(final LogicalScaArtifact<?> scaArtifact) {

        LogicalScaArtifact<?> temp = scaArtifact;

        Set<QName> intentNames = new LinkedHashSet<QName>();
        while (temp != null) {
            intentNames.addAll(temp.getIntents());
            temp = temp.getParent();
        }
        return intentNames;

    }

    /*
     * Expand profile intents.
     */
    protected Set<Intent> resolveProfileIntents(Set<QName> intentNames) throws PolicyResolutionException {

        Set<Intent> requiredIntents = new LinkedHashSet<Intent>();

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
