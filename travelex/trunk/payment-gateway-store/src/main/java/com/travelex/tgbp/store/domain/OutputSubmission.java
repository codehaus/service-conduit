package com.travelex.tgbp.store.domain;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Represents group of instructions which are sent out in a same output file.
 */
public class OutputSubmission {

    private Long id;
    private ClearingMechanism clearingMechanism;

    /**
     * Initialises with attributes.
     *
     * @param clearingMechanism - target clearing mechanism.
     */
    public OutputSubmission(ClearingMechanism clearingMechanism) {
        this.clearingMechanism = clearingMechanism;
    }

    /**
     * Returns submission identifier.
     *
     * @return submission id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns target clearing mechanism.
     *
     * @return clearing mechanism.
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

}
