package com.travelex.tgbp.routing.types;

import java.math.BigDecimal;

/**
 * Represents payment output routing configuration which is based on exact payment amount match.
 */
public class RoutingConfigOnAmount extends AbstractRoutingConfig {

    private BigDecimal amount;

    /**
     * Returns associated payment amount.
     *
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

}
