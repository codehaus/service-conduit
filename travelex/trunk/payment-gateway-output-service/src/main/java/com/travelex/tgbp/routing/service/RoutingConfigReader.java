package com.travelex.tgbp.routing.service;

import java.util.Collection;

import com.travelex.tgbp.store.domain.routing.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.type.Currency;

/**
 * Reader service for routing configuration.
 */
public interface RoutingConfigReader {

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
