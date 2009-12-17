package com.travelex.tgbp.routing.impl;

import java.math.BigDecimal;
import java.util.Collection;

import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.scope.Composite;

import com.travelex.tgbp.routing.service.RoutingConfigReader;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.routing.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Resolves payment clearing mechanism on instruction value date and amount.
 */
@Composite
public class ValueDateBasedResolver extends AbstractClearingMechanismResolver {

    @Reference protected RoutingConfigReader configReader;

    /**
     * {@inheritDoc}
     */
    @Override
    public ClearingMechanism resolve(Instruction instruction) {
        final Collection<RoutingConfigOnValueDate> routingConfigs = configReader.onValueDate(instruction.getCurrency(), daysFromToday(instruction.getValueDate()));
        return evaluateClearingMechOnThresholdAmount(instruction.getAmount(), routingConfigs);
    }

    /*
     * Iterates over given collection of routing configurations (ordered on transfer charge)
     * and returns clearing mechanism if given instruction amount meets configured threshold amount.
     */
    private ClearingMechanism evaluateClearingMechOnThresholdAmount(BigDecimal instructionAmount, Collection<RoutingConfigOnValueDate> routingConfigs) {
        for (RoutingConfigOnValueDate config : routingConfigs) {
            if (config.getThresholdAmount().compareTo(instructionAmount) >= 0) {
                return config.getClearingMechanism();
            }
        }
        return null;
    }

}
