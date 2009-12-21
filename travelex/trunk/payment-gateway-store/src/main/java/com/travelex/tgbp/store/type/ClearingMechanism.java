package com.travelex.tgbp.store.type;

/**
 * Enumeration of supported payment types.
 */
public enum ClearingMechanism {

    BACS (Currency.GBP, "MT103"),

    CHAPS (Currency.GBP, "MT103"),

    FPS (Currency.GBP, "MT103"),

    NACHA (Currency.USD, "NACHA"),

    FEDWIRE (Currency.USD, "FEDWIRE");

    private Currency currency;

    private String fileFormat;

    /**
     * Intialises with attributes.
     *
     * @param currency - supported currency type.
     */
    private ClearingMechanism(Currency currency, String fileFormat) {
        this.currency = currency;
        this.fileFormat = fileFormat;
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @return the fileFormat
     */
    public String getFileFormat() {
        return fileFormat;
    }

}
