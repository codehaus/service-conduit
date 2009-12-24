package com.travelex.tgbp.output.service.event;

import com.travelex.tgb.event.notifier.api.EventDescriptor;

public class OutputEvent implements EventDescriptor {

    private int outputCount;
    private String[] currencyValues;
    private String mostRecentRoute;

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

}
