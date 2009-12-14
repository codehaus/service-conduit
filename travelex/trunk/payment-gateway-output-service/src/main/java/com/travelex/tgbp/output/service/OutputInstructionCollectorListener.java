package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Call back interface for {@link OutputInstructionCollector}.
 */
@Conversational
public interface OutputInstructionCollectorListener {

    /**
     * Notifies end of output processing related to specific currency.
     *
     * @param clearingMechanism - payment type whose instruction output processing is completed.
     */
    void onOutputCompletion(ClearingMechanism clearingMechanism);
}
