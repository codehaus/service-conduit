/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.fabric.services.expression;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.host.expression.ExpressionEvaluator;
import org.sca4j.host.expression.ExpressionExpansionException;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 14:43:51 -0700 (Mon, 31 Mar 2008) $
 */
public class ExpressionExpanderImplTestCase extends TestCase {

    public void testBeginExpansion() throws Exception {
        ExpressionEvaluator evaluator = EasyMock.createMock(ExpressionEvaluator.class);
        EasyMock.expect(evaluator.evaluate("expr1")).andReturn("expression1");
        EasyMock.replay(evaluator);
        Map<Integer, ExpressionEvaluator> evaluators = new HashMap<Integer, ExpressionEvaluator>();
        evaluators.put(0, evaluator);
        ExpressionExpanderImpl expander = new ExpressionExpanderImpl();
        expander.setEvaluators(evaluators);
        String expression = "${expr1} this is a test";
        String result = expander.expand(expression);
        assertEquals("expression1 this is a test", result);
        EasyMock.verify(evaluator);
    }

    public void testEndExpansion() throws Exception {
        ExpressionEvaluator evaluator = EasyMock.createMock(ExpressionEvaluator.class);
        EasyMock.expect(evaluator.evaluate("expr1")).andReturn("expression1");
        EasyMock.replay(evaluator);
        Map<Integer, ExpressionEvaluator> evaluators = new HashMap<Integer, ExpressionEvaluator>();
        evaluators.put(0, evaluator);
        ExpressionExpanderImpl expander = new ExpressionExpanderImpl();
        expander.setEvaluators(evaluators);
        String expression = "this is a ${expr1}";
        String result = expander.expand(expression);
        assertEquals("this is a expression1", result);
        EasyMock.verify(evaluator);
    }

    public void testBeginEndExpansion() throws Exception {
        ExpressionEvaluator evaluator = EasyMock.createMock(ExpressionEvaluator.class);
        EasyMock.expect(evaluator.evaluate("expr1")).andReturn("expression1");
        EasyMock.expect(evaluator.evaluate("expr2")).andReturn("expression2");
        EasyMock.replay(evaluator);
        Map<Integer, ExpressionEvaluator> evaluators = new HashMap<Integer, ExpressionEvaluator>();
        evaluators.put(0, evaluator);
        ExpressionExpanderImpl expander = new ExpressionExpanderImpl();
        expander.setEvaluators(evaluators);
        String expression = "${expr1} this is a ${expr2}";
        String result = expander.expand(expression);
        assertEquals("expression1 this is a expression2", result);
        EasyMock.verify(evaluator);
    }

    public void testMultipleExpansion() throws Exception {
        ExpressionEvaluator evaluator = EasyMock.createMock(ExpressionEvaluator.class);
        EasyMock.expect(evaluator.evaluate("expr1")).andReturn("expression1");
        EasyMock.expect(evaluator.evaluate("expr2")).andReturn("expression2");
        EasyMock.replay(evaluator);
        Map<Integer, ExpressionEvaluator> evaluators = new HashMap<Integer, ExpressionEvaluator>();
        evaluators.put(0, evaluator);
        ExpressionExpanderImpl expander = new ExpressionExpanderImpl();
        expander.setEvaluators(evaluators);
        String expression = "this ${expr1} is a ${expr2} string";
        String result = expander.expand(expression);
        assertEquals("this expression1 is a expression2 string", result);
        EasyMock.verify(evaluator);
    }

    public void testInvalidExpansion() throws Exception {
        ExpressionEvaluator evaluator = EasyMock.createMock(ExpressionEvaluator.class);
        EasyMock.replay(evaluator);
        Map<Integer, ExpressionEvaluator> evaluators = new HashMap<Integer, ExpressionEvaluator>();
        evaluators.put(0, evaluator);
        ExpressionExpanderImpl expander = new ExpressionExpanderImpl();
        expander.setEvaluators(evaluators);
        String expression = "this is a bad ${expr1";
        try {
            expander.expand(expression);
            fail("Invalid expression not caught");
        } catch (ExpressionExpansionException e) {
            // expected
        }
        EasyMock.verify(evaluator);
    }

    public void testNoExpression() throws Exception {
        ExpressionEvaluator evaluator = EasyMock.createMock(ExpressionEvaluator.class);
        EasyMock.expect(evaluator.evaluate("expr1")).andReturn(null);
        EasyMock.replay(evaluator);
        Map<Integer, ExpressionEvaluator> evaluators = new HashMap<Integer, ExpressionEvaluator>();
        evaluators.put(0, evaluator);
        ExpressionExpanderImpl expander = new ExpressionExpanderImpl();
        expander.setEvaluators(evaluators);
        String expression = "this is a ${expr1}";
        try {
            expander.expand(expression);
            fail("ValueNotFoundException for expression not caught");
        } catch (ValueNotFoundException e) {
            // expected
        }
        EasyMock.verify(evaluator);
    }

}
