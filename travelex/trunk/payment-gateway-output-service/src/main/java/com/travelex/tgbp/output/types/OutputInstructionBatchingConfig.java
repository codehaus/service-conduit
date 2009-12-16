package com.travelex.tgbp.output.types;

import java.math.BigDecimal;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Configuration type describing output instruction batching parameters.
 */
public class OutputInstructionBatchingConfig {

    private ClearingMechanism clearingMechanism;
    private int thresholdCount;
    private BigDecimal thresholdAmount;

    /**
     * @return the clearingMechanism
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

    /**
     * @return the thresholdCount
     */
    public int getThresholdCount() {
        return thresholdCount;
    }

    /**
     * @return the thresholdAmount
     */
    public BigDecimal getThresholdAmount() {
        return thresholdAmount;
    }

}
