package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Call back interface for {@link OutputInstructionCreationService}.
 */
@Conversational
public interface OutputInstructionCreationServiceListener {

    /**
     * Notifies end of output instruction creation related to specific clearing mechanism.
     *
     * @param clearingMechanism - payment type whose output instruction creation is complete.
     */
    void onCompletion(ClearingMechanism clearingMechanism);
}
