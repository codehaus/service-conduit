package com.travelex.tgbp.rules.dynamic.capture;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.rules.dynamic.capture.api.DynamicRuleSubmissionProcessor;

import junit.framework.TestCase;

public class DynamicRuleSubmissionProcessorITest extends TestCase {

    @Reference protected DynamicRuleSubmissionProcessor dynamicRuleSubmissionProcessor;

    public void testRuleCreation() throws Exception {
        InputStream ruleData = Thread.currentThread().getContextClassLoader().getResourceAsStream("input.xml");
        dynamicRuleSubmissionProcessor.createRule("RULE1", "EBA", IOUtils.toString(ruleData), "pain.001.001.03");
    }

}
