package com.travelex.tgbp.routing.impl;

import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.scope.Composite;

import com.travelex.tgbp.routing.service.RoutingConfigReader;
import com.travelex.tgbp.routing.types.RoutingConfigOnAmount;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Resolves payment clearing mechanism on instruction amount.
 *
 * Its a priority resolver which performs exact match on instruction amount with configured value.
 */
@Composite
public class AmountBasedResolver extends AbstractClearingMechanismResolver {

    @Reference protected RoutingConfigReader configReader;

    /**
     * {@inheritDoc}
     */
    @Override
    public ClearingMechanism resolve(Instruction instruction) {
        final RoutingConfigOnAmount routingConfig = configReader.onAmount(instruction.getCurrency(), instruction.getAmount());
        if (routingConfig != null && routingConfig.getClearingPeriod() <= daysFromToday(instruction.getValueDate())) {
            return routingConfig.getClearingMechanism();
        } else {
            return null;
        }
    }

}
