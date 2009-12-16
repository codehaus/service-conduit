package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.type.Currency;

/**
 * Call back interface for {@link OutputInitiator}.
 */
@Conversational
public interface OutputInitiatorListener {

    /**
     * Notifies end of output instruction creation related to specific currency.
     *
     * @param currency - currency whose output instruction creation is complete.
     */
    void onCompletion(Currency currency);
}
