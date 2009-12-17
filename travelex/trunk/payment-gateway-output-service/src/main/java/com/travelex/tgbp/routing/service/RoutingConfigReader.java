package com.travelex.tgbp.routing.service;

import java.math.BigDecimal;
import java.util.Collection;

import com.travelex.tgbp.store.domain.routing.RoutingConfigOnAmount;
import com.travelex.tgbp.store.domain.routing.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.type.Currency;

/**
 * Reader service for routing configuration.
 */
public interface RoutingConfigReader {

    /**
     * Searches for routing configuration based on given currency and amount.
     * It does exact comparison on amount and currency.
     *
     * @param currency - payment currency.
     * @param amount - payment amount.
     * @return found configuration object; null otherwise.
     */
    RoutingConfigOnAmount onAmount(Currency currency, BigDecimal amount);

    /**
     * Searches for routing configuration based on given currency and maximum allowed clearing days.
     * Returns search result in ascending order of clearing charge.
     *
     * @param currency - payment currency to match.
     * @param maxClearingPeriod - maximum clearing period limit (in days)
     * @return collection of matching configuration objects; null otherwise.
     */
    Collection<RoutingConfigOnValueDate> onValueDate(Currency currency, int maxClearingPeriod);

}
