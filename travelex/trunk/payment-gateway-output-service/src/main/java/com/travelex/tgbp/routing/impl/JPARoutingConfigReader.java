package com.travelex.tgbp.routing.impl;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.travelex.tgbp.routing.service.RoutingConfigReader;
import com.travelex.tgbp.store.domain.routing.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.type.Currency;

/**
 * JPA implementation of {@link RoutingConfigReader}.
 */
public class JPARoutingConfigReader implements RoutingConfigReader {

    @PersistenceContext(unitName = "tgbp-store") protected EntityManager entityManager;

    private static final String FIND_CONFIG_ON_VALUE_DATE = "FIND_CONFIG_ON_VALUE_DATE";

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
