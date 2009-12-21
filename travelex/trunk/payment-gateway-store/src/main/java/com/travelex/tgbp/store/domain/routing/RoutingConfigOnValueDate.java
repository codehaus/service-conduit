package com.travelex.tgbp.store.domain.routing;

import java.math.BigDecimal;

import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Represents payment output routing configuration which is based on transfer cost/clearing period.
 */
public class RoutingConfigOnValueDate {

    private Long id;
    private Currency currency;
    private ClearingMechanism clearingMechanism;
    private BigDecimal thresholdAmount;
    private int clearingPeriod;
    private double charge;

    /**
     * JPA constructor
     */
    RoutingConfigOnValueDate() { }

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

    /**
     * Returns associated threshold amount.
     *
     * @return threshold amount
     */
    public BigDecimal getThresholdAmount() {
        return thresholdAmount;
    }

    /**
     * Returns associated transaction charge.
     *
     * @return payment transfer transaction charge.
     */
    public double getCharge() {
        return charge;
    }

}
