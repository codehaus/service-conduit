package com.travelex.tgbp.store.domain;

import java.math.BigDecimal;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Represents instruction which has been sent out.
 */
public class OutputInstruction extends PersistentEntity {

    private Long outputSubmissionId;
    private ClearingMechanism clearingMechanism;
    private String sourceIdentifier;
    private String targetIdentifier;
    private String outputPaymentData;
    private BigDecimal amount;

    /**
     * Initialises with attributes.
     *
     * @param outputSubmissionId - output submission id.
     * @param sourceIdentifier -  source identifier.
     * @param targetIdentifier - target identifier.
     * @param amount - payment amount.
     */
    public OutputInstruction(ClearingMechanism clearingMechanism, String sourceIdentifier, String targetIdentifier, BigDecimal amount) {
        this.clearingMechanism = clearingMechanism;
        this.sourceIdentifier = sourceIdentifier;
        this.targetIdentifier = targetIdentifier;
        this.amount = amount;
    }

    /**
     * Returns instruction id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns output submission id.
     *
     * @return the outputSubmissionId
     */
    public Long getOutputSubmissionId() {
        return outputSubmissionId;
    }

    /**
     * Returns instruction source identifier.
     *
     * @return the sourceIdentifier
     */
    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    /**
     * Returns instruction target identifier.
     *
     * @return the targetIdentifier
     */
    public String getTargetIdentifier() {
        return targetIdentifier;
    }

    /**
     * Returns the target clearing mechanism
     *
     * @return the clearingMechanism
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

    /**
     * Returns the instruction amount
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns output payment data transformed from input payment data
     *
     * @return the outputPaymentData
     */
    public String getOutputPaymentData() {
        return outputPaymentData;
    }

    /**
     * Sets output payment data
     *
     * @param outputPaymentData data to set
     */
    public void setOutputPaymentData(String outputPaymentData) {
        this.outputPaymentData = outputPaymentData;
    }

}
