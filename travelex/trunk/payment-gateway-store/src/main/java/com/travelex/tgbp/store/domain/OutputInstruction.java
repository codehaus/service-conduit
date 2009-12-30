package com.travelex.tgbp.store.domain;

import java.math.BigDecimal;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

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
    @Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
    private LocalDate valueDate;

    /**
     * JPA Constructor
     */
    OutputInstruction() { }

    /**
     * Initialises with attributes.
     *
     * @param outputSubmissionId - output submission id.
     * @param sourceIdentifier -  source identifier.
     * @param targetIdentifier - target identifier.
     * @param amount - payment amount.
     */
    public OutputInstruction(ClearingMechanism clearingMechanism, LocalDate valueDate, BigDecimal amount) {
        this.clearingMechanism = clearingMechanism;
        this.valueDate = valueDate;
        this.amount = amount;
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

    /**
     * Assign output Submission
     * @param outputSubmissionId
     */
    public void assignSubmission(Long outputSubmissionId){
    	this.outputSubmissionId = outputSubmissionId;
    }

    /**
     * @return the valueDate
     */
    public LocalDate getValueDate() {
        return valueDate;
    }

}
