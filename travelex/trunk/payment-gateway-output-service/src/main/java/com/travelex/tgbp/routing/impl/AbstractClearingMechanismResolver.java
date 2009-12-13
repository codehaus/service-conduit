package com.travelex.tgbp.routing.impl;

import org.joda.time.LocalDate;

import com.travelex.tgbp.routing.service.ClearingMechanismResolver;

/**
 * Abstract implementation of {@link ClearingMechanismResolver}.
 */
public abstract class AbstractClearingMechanismResolver implements ClearingMechanismResolver {

    /**
     * Evaluates difference of days between given date and today.
     */
    protected int daysFromToday(LocalDate valueDate) {
        final LocalDate today = new LocalDate();
        final int yearDifference = valueDate.getYear() - today.getYear();
        return valueDate.getDayOfYear() - today.getDayOfYear() + (yearDifference * 365);
    }

}
