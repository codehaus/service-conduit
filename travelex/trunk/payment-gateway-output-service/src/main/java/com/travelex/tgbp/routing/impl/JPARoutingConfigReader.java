package com.travelex.tgbp.routing.impl;

import java.math.BigDecimal;
import java.util.Collection;

import com.travelex.tgbp.routing.service.RoutingConfigReader;
import com.travelex.tgbp.routing.types.RoutingConfigOnAmount;
import com.travelex.tgbp.routing.types.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.type.Currency;

/**
 * JPA implementation of {@link RoutingConfigReader}.
 */
public class JPARoutingConfigReader implements RoutingConfigReader {

    /**
     * {@inheritDoc}
     */
    @Override
    public RoutingConfigOnAmount onAmount(Currency currency, BigDecimal amount) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RoutingConfigOnValueDate> onValueDate(Currency currency, int maxClearingPeriod) {
        // TODO Auto-generated method stub
        return null;
    }

}
