package com.travelex.tgbp.store.domain.rule;

import com.travelex.tgbp.store.domain.PersistentEntity;

public class DynamicRule extends PersistentEntity {

    private final String clearingMechanism;
    private final String ruleName;
    private final String ruleText;
    private final String schema;
    private final int priority;

    public DynamicRule(String routeTo, String ruleName, String ruleText, String appliesTo) {
        this.clearingMechanism = routeTo;
        this.ruleName = ruleName;
        this.ruleText = ruleText;
        this.schema = appliesTo;
        this.priority = 1; //Unused - probably wont have time to order rules
    }

    public String getClearingMechanism() {
        return clearingMechanism;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleText() {
        return ruleText;
    }

    public String getSchema() {
        return schema;
    }

    public int getPriority() {
        return priority;
    }
}
