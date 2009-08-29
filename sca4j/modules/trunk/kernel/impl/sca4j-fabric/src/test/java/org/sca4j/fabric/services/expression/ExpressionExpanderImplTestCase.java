/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
