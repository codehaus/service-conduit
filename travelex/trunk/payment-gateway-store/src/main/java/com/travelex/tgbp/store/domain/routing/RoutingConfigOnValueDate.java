package com.travelex.tgbp.store.domain.routing;

import java.math.BigDecimal;

import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Represents payment output routing configuration which is based on transfer cost/clearing period.
 */
public class RoutingConfigOnValueDate extends AbstractRoutingConfig {

    private BigDecimal thresholdAmount;
    private double charge;
    private Long id;
    private Currency currency;
    private int clearingPeriod;

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
