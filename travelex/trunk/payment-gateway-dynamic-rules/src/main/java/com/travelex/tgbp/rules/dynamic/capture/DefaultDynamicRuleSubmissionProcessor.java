package com.travelex.tgbp.rules.dynamic.capture;

import com.travelex.tgbp.rules.dynamic.capture.api.DynamicRuleSubmissionProcessor;

public class DefaultDynamicRuleSubmissionProcessor implements DynamicRuleSubmissionProcessor {

    public String createRule(String ruleName, String clearingMechanism, String ruleData) {
        System.out.println(ruleName);
        System.out.println(clearingMechanism);
        System.out.println(ruleData);

        return "OK";
    }


}
