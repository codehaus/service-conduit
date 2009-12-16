package com.travelex.tgbp.routing.impl;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.travelex.tgbp.routing.service.RoutingConfigReader;
import com.travelex.tgbp.routing.types.RoutingConfigOnAmount;
import com.travelex.tgbp.routing.types.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.type.Currency;

/**
 * JPA implementation of {@link RoutingConfigReader}.
 */
public class JPARoutingConfigReader implements RoutingConfigReader {

    @PersistenceContext(unitName = "tgbp-output") protected EntityManager entityManager;

    private static final String FIND_CONFIG_ON_AMOUNT = "FIND_CONFIG_ON_AMOUNT";
    private static final String FIND_CONFIG_ON_VALUE_DATE = "FIND_CONFIG_ON_VALUE_DATE";

    /**
     * {@inheritDoc}
     */
    @Override
    public RoutingConfigOnAmount onAmount(Currency currency, BigDecimal amount) {
        final Query query = entityManager.createNamedQuery(FIND_CONFIG_ON_AMOUNT);
        query.setParameter(1, currency);
        query.setParameter(2, amount);
        return (RoutingConfigOnAmount) query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<RoutingConfigOnValueDate> onValueDate(Currency currency, int maxClearingPeriod) {
        final Query query = entityManager.createNamedQuery(FIND_CONFIG_ON_VALUE_DATE);
        query.setParameter(1, currency);
        query.setParameter(2, maxClearingPeriod);
        return query.getResultList();
    }

}
