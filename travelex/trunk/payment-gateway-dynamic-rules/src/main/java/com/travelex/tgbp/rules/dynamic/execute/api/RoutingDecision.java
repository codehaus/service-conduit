package com.travelex.tgbp.rules.dynamic.execute.api;

public class RoutingDecision {

    private final boolean routeSpecified;
    private String clearingMechanism;
    private String ruleName;

    public RoutingDecision(boolean routeSpecified) {
        this.routeSpecified = routeSpecified;
    }

    public RoutingDecision(boolean routeSpecified, String clearingMechanism, String ruleName) {
        this(routeSpecified);
        this.clearingMechanism = clearingMechanism;
        this.ruleName = ruleName;
    }

    public boolean routeSpecified() {
        return routeSpecified;
    }

    public String getClearingMechanism() {
        return clearingMechanism;
    }

    public Object getRuleName() {
        return ruleName;
    }

}
