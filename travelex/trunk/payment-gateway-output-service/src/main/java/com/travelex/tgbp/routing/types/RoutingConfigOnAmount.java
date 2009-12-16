package com.travelex.tgbp.routing.types;

import java.math.BigDecimal;

import com.travelex.tgbp.store.type.Currency;

/**
 * Represents payment output routing configuration which is based on exact payment amount match.
 */
public class RoutingConfigOnAmount extends AbstractRoutingConfig {

    private BigDecimal amount;
    private Long id;
    private Currency currency;

    /**
     * Returns associated payment amount.
     *
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

}
