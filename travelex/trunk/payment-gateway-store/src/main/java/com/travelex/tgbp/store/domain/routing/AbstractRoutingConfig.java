package com.travelex.tgbp.store.domain.routing;

import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Abstract representation of output routing configuration.
 */
public class AbstractRoutingConfig {

    private Long id;
    private Currency currency;
    private ClearingMechanism clearingMechanism;
    private int clearingPeriod;

    /**
     * Returns associated currency.
     *
     * @return currency.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Returns associated clearing mechanism.
     *
     * @return clearing mechanism.
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

    /**
     * Returns associated clearing period.
     *
     * @return clearing period.
     */
    public int getClearingPeriod() {
        return clearingPeriod;
    }

}
