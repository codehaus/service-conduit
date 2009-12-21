package com.travelex.tgbp.rules.dynamic.execute;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.rules.dynamic.execute.api.DynamicRules;

public class DynamicRuleExecutionITest extends TestCase {

    @Reference protected DynamicRules dynamicRules;

    public void testExecution() throws Exception {
        assertNotNull(dynamicRules);
    }

}
