package com.travelex.tgbp.store.domain;

import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Represents group of instructions which are sent out in a same output file.
 */
public class OutputSubmission {

    private Long id;
    private Currency currency;
    private ClearingMechanism clearingMechanism;

    /**
     *
     * @param currency
     * @param clearingMechanism
     */
    public OutputSubmission(Currency currency, ClearingMechanism clearingMechanism) {
        this.currency = currency;
        this.clearingMechanism = clearingMechanism;
    }

    public Long getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

}
