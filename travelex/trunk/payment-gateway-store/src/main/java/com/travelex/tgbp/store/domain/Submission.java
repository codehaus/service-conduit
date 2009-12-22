package com.travelex.tgbp.store.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

/**
 * Represents file submission entity.
 */
public class Submission extends PersistentEntity {

    private String messageId;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
    private LocalDate submissionDate;
    private byte[] inputData;
    private String fileName;

    //The submission header is the root of the document and the header without any child elements.
    //This partially duplicates the content of inputData so we'd have to think about a better strategy
    //if we came to do this work for real. It falls in to the same category as having the payment group
    //xml on each instruction.
    private String submissionHeader;

    public Submission(String fileName, byte[] inputData) {
        this.fileName = fileName;
        this.inputData = inputData;
        this.submissionDate = new LocalDate();
    }

    /**
     * JPA Constructor
     */
    Submission() {

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
     * Returns submission date
     *
     * @return the submissionDate
     */
    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Returns the submission data.
     * @return data - as a stream
     */
    public InputStream getDataAsStream() {
        return new ByteArrayInputStream(inputData);
    }

    /**
     * Returns file name
     *
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the submission header which is the root of the document and the header without any child elements.
     * @returns submissionHeader
     */
    public String getSubmissionHeader() {
        return submissionHeader;
    }

    /**
     * Sets message id
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * The submission header is the root of the document and the header without any child elements.
     * @param submissionHeader
     */
    public void setSubmissionHeader(String submissionHeader) {
        this.submissionHeader = submissionHeader;
    }

}
