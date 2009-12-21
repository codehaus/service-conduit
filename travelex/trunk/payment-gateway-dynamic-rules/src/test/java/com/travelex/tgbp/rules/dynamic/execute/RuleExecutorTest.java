package com.travelex.tgbp.rules.dynamic.execute;

import static com.travelex.tgbp.rules.dynamic.execute.Operator.EQUALS;
import static com.travelex.tgbp.rules.dynamic.execute.Operator.LESS_THAN;
import junit.framework.TestCase;


public class RuleExecutorTest extends TestCase {

    public void testEqual() throws Exception {

        String v1 = "V1";
        String v2 = "V1";
        String javaType = "java.lang.String";

        if(!RuleExecutor.passes(v1, v2, EQUALS, javaType)) {
            fail();
        }

        v1 = "34675";
        v2 = "34675";
        javaType = "java.lang.Integer";

        if(!RuleExecutor.passes(v1, v2, EQUALS, javaType)) {
            fail();
        }

        v1 = "34675.30";
        v2 = "34675.3";
        javaType = "java.math.BigDecimal";

        if(!RuleExecutor.passes(v1, v2, EQUALS, javaType)) {
            fail();
        }

        v1 = "2002-09-24";
        v2 = "2002-09-24";
        javaType = "java.lang.String"; //Compare dates as Strings

        if(!RuleExecutor.passes(v1, v2, EQUALS, javaType)) {
            fail();
        }

    }

    public void testLessThan() throws Exception {

        String v1 = "3";
        String v2 = "15";
        String javaType = "java.lang.Integer";

        if(!RuleExecutor.passes(v1, v2, LESS_THAN, javaType)) {
            fail();
        }


        v1 = "ABC";
        v2 = "XBC";
        javaType = "java.lang.String";

        if(!RuleExecutor.passes(v1, v2, LESS_THAN, javaType)) {
            fail();
        }

        v1 = "34675.50";
        v2 = "34675.57";
        javaType = "java.math.BigDecimal";

        if(!RuleExecutor.passes(v1, v2, LESS_THAN, javaType)) {
            fail();
        }

        v1 = "2001-09-24";
        v2 = "2002-09-24";
        javaType = "java.lang.String"; //Compare dates as Strings

        if(!RuleExecutor.passes(v1, v2, LESS_THAN, javaType)) {
            fail();
        }

    }
}
