package com.travelex.tgbp.processor.event;

import com.travelex.tgb.event.notifier.api.EventDescriptor;

public class SubmissionEvent implements EventDescriptor {

    private int submissionCount; //Total count in the system for today
    private String[] currencyValues; //e.g. [0]=EUR/1000, [2]=USD/23.52
    private int instructionCount; //Total instruction count for the current submission
    private String submissionId;
    private String fileName;
    private String fileContent;

    @Override
    public String getMessageId() {
        return "SUBMISSION_CAPTURED";
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }

    public String[] getCurrencyValues() {
        return currencyValues;
    }

    public void setCurrencyValues(String[] currencyValues) {
        this.currencyValues = currencyValues;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public void setInstructionCount(int instructionCount) {
        this.instructionCount = instructionCount;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the fileContent
     */
    public String getFileContent() {
        return fileContent;
    }

    /**
     * @param fileContent the fileContent to set
     */
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

}
