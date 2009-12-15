package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Call back interface for {@link OutputInstructionCollector}.
 */
@Conversational
public interface OutputInstructionCollectorListener {

    /**
     * Notifies end of output processing related to specific clearing mechanism.
     *
     * @param clearingMechanism - payment type whose instruction output processing is completed.
     */
    void onCompletion(ClearingMechanism clearingMechanism);
}
