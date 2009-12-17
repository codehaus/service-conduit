package com.travelex.tgbp.rules.dynamic.capture;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.rules.dynamic.capture.api.DynamicRuleSubmissionProcessor;
import com.travelex.tgbp.store.domain.rule.DynamicRule;
import com.travelex.tgbp.store.service.api.DataStore;

public class DefaultDynamicRuleSubmissionProcessor implements DynamicRuleSubmissionProcessor {

    @Reference protected DataStore dataStore;

    public String createRule(String ruleName, String clearingMechanism, String ruleData, String appliesTo) {
        //Just store the xpath as raw xml
        DynamicRule rule = new DynamicRule(clearingMechanism, ruleName, ruleData, appliesTo);
        dataStore.store(rule);
        return "OK";
    }


}
