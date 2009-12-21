package com.travelex.tgbp.routing.impl;

import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.scope.Composite;

import com.travelex.tgbp.routing.service.ClearingMechanismResolver;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Composition of routing mechanism resolvers.
 */
@Composite
public class CompositeResolver implements ClearingMechanismResolver {

    @Reference protected ClearingMechanismResolver dynamicRuleResolver;
    @Reference protected ClearingMechanismResolver valueDateAmountResolver;

    /**
     * {@inheritDoc}
     */
    @Override
    public ClearingMechanism resolve(Instruction instruction) {
        final ClearingMechanism clearing = dynamicRuleResolver.resolve(instruction);
        if (clearing != null) {
            return clearing;
        } else {
            return valueDateAmountResolver.resolve(instruction);
        }
    }

}
