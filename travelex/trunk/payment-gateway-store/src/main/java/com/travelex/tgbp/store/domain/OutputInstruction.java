package com.travelex.tgbp.store.domain;

/**
 * Represents instruction which has been sent out.
 */
public class OutputInstruction {

    private Long id;
    private Long outputSubmissionId;
    private String sourceIdentifier;
    private String targetIdentifier;

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
