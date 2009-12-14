package com.travelex.tgbp.store.domain;

import java.math.BigDecimal;

/**
 * Represents instruction which has been sent out.
 */
public class OutputInstruction {

    private Long id;
    private Long outputSubmissionId;
    private String sourceIdentifier;
    private String targetIdentifier;
    private BigDecimal amount;

    /**
     * Initialises with attributes.
     *
     * @param outputSubmissionId - output submission id.
     * @param sourceIdentifier -  source identifier.
     * @param targetIdentifier - target identifier.
     * @param amount - payment amount.
     */
    public OutputInstruction(Long outputSubmissionId, String sourceIdentifier, String targetIdentifier, BigDecimal amount) {
        this.outputSubmissionId = outputSubmissionId;
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

}
