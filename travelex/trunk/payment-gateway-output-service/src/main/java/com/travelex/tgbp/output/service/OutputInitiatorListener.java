package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.type.Currency;

/**
 * Call back listener for {@link OutputInitiator}.
 */
@Conversational
public interface OutputInitiatorListener {

    /**
     * Notifies end of output processing related to specific currency.
     *
     * @param currency - currency whose instruction output processing is completed.
     */
    void onCompletion(Currency currency);

    /**
     * Notifies end of output processing.
     */
    void onCompletion();
}
