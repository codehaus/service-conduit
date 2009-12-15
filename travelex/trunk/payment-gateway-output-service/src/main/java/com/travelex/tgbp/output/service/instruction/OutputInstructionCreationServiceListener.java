package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.type.Currency;

/**
 * Call back serviceListener for {@link OutputInstructionCreationService}.
 */
@Conversational
public interface OutputInstructionCreationServiceListener {

    /**
     * Notifies end of output processing related to specific currency.
     *
     * @param currency - currency whose instruction output processing is completed.
     */
    void onCompletion(Currency currency);
}
