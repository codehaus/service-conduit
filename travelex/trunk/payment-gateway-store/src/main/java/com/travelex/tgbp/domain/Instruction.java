package com.travelex.tgbp.domain;

/**
 * Represents payment record entity.
 */
public class Instruction {

    private Long id;
    private Long submissionId;

    /**
     * Initialises with submission identifier.
     *
     * @param submissionId - submission identifier.
     */
    public Instruction(Long submissionId) {
        this.submissionId = submissionId;
    }

    /**
     * Returns entity identifier.
     *
     * @return the entity id
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns parent submission identifier.
     *
     * @return the submission id
     */
    public Long getSubmissionId() {
        return submissionId;
    }

}
