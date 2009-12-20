package com.travelex.tgbp.store.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.joda.time.LocalDate;

/**
 * Represents file submission entity.
 */
public class Submission {

    private Long id;
    private String messageId;
    @SuppressWarnings("unused")
    private LocalDate submissionDate;
    private byte[] inputData;

    public Submission(String messageId) {
        this.messageId = messageId;
        this.submissionDate = new LocalDate();
    }

    /**
     * JPA Constructor
     */
    Submission() {

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
     * Returns input submission message id.
     *
     * @return message id.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns the submission data.
     * @return data - as a stream
     */
    public InputStream getDataAsStream() {
        return new ByteArrayInputStream(inputData);
    }

    /**
     * Sets message id
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
