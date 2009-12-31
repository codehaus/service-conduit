package com.travelex.tgbp.output.service.event;

import com.travelex.tgb.event.notifier.api.EventDescriptor;

public class OutputEvent implements EventDescriptor {

    private int outputCount;
    private String[] currencyValues;
    private String mostRecentSubId;
    private String mostRecentRoute;
    private String mostRecentFileName;
    private String mostRecentFileContent;

    @Override
    public String getMessageId() {
        return "OUTPUT";
    }

    public int getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(int outputCount) {
        this.outputCount = outputCount;
    }

    public String[] getCurrencyValues() {
        return currencyValues;
    }

    public void setCurrencyValues(String[] currencyValues) {
        this.currencyValues = currencyValues;
    }

    public String getMostRecentRoute() {
        return mostRecentRoute;
    }

    public void setMostRecentRoute(String mostRecentRoute) {
        this.mostRecentRoute = mostRecentRoute;
    }

    /**
     * @return the mostRecentFileName
     */
    public String getMostRecentFileName() {
        return mostRecentFileName;
    }

    /**
     * @param mostRecentFileName the mostRecentFileName to set
     */
    public void setMostRecentFileName(String mostRecentFileName) {
        this.mostRecentFileName = mostRecentFileName;
    }

    /**
     * @return the mostRecentFileContent
     */
    public String getMostRecentFileContent() {
        return mostRecentFileContent;
    }

    /**
     * @param mostRecentFileContent the mostRecentFileContent to set
     */
    public void setMostRecentFileContent(String mostRecentFileContent) {
        this.mostRecentFileContent = mostRecentFileContent;
    }

    /**
     * @return the mostRecentSubId
     */
    public String getMostRecentSubId() {
        return mostRecentSubId;
    }

    /**
     * @param mostRecentSubId the mostRecentSubId to set
     */
    public void setMostRecentSubId(String mostRecentSubId) {
        this.mostRecentSubId = mostRecentSubId;
    }

}
