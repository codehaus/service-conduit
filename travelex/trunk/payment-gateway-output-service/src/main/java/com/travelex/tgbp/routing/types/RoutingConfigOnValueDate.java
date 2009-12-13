package com.travelex.tgbp.routing.types;

import java.math.BigDecimal;

/**
 * Represents payment output routing configuration which is based on transfer cost/clearing period.
 */
public class RoutingConfigOnValueDate extends AbstractRoutingConfig {

    private BigDecimal thresholdAmount;
    private double charge;

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
