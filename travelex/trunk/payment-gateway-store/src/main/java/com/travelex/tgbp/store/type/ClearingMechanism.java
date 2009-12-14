package com.travelex.tgbp.store.type;

/**
 * Enumeration of supported payment types.
 */
public enum ClearingMechanism {

    BACS (Currency.GBP),

    CHAPS (Currency.GBP),

    FPS (Currency.GBP),

    NACHA (Currency.USD),

    FEDWIRE (Currency.USD);

    private Currency currency;

    /**
     * Intialises with currency.
     *
     * @param currency - supported currency type.
     */
    private ClearingMechanism(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

}
