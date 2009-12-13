package com.travelex.tgbp.routing.service;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Provides routines to identify clearing mechanism to be used for given payment instruction.
 */
public interface ClearingMechanismResolver {

    /**
     * Resolves clearing mechanism for given instruction using configured parameters.
     *
     * @param instruction - payment instruction.
     * @return identified clearing mechanism; null if payment instruction doesn't meet routing criteria.
     */
    ClearingMechanism resolve(Instruction instruction);
}
