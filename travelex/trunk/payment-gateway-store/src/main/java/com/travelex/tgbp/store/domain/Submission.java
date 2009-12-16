package com.travelex.tgbp.store.domain;

import org.joda.time.LocalDate;

/**
 * Represents file submission entity.
 */
public class Submission {

    private Long id;
    private String messageId;
    @SuppressWarnings("unused")
    private LocalDate submissionDate;

    public Submission(String messageId) {
        this.messageId = messageId;
        this.submissionDate = new LocalDate();
    }

    /**
     * Returns entity identifier.
     *
     * @return the entity id
     */
    public Long getId() {
        return id;
    }

    public String getMessageId() {
        return messageId;
    }

    /**
     * JPA Constructor
     */
    Submission() {

    }

}
