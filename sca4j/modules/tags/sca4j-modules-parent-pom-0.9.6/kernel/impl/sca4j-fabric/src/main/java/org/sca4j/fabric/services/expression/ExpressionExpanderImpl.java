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
 * Original Codehaus Header
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
 * Original Apache Header
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

import java.util.Map;
import java.util.TreeMap;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.host.expression.ExpressionEvaluator;
import org.sca4j.host.expression.ExpressionExpander;
import org.sca4j.host.expression.ExpressionExpansionException;

/**
 * Expands strings containing expressions delimited by '${' and '}' through delegation to a set of ExpressionEvaluators.
 *
 * @version $Rev: 3524 $ $Date: 2008-03-31 14:43:51 -0700 (Mon, 31 Mar 2008) $
 */
public class ExpressionExpanderImpl implements ExpressionExpander {
    private static final String PREFIX = "${";
    private static final String POSTFIX = "}";

    /* evaluators sorted by their configured order */
    private TreeMap<Integer, ExpressionEvaluator> evaluators = new TreeMap<Integer, ExpressionEvaluator>();

    @Reference
    public void setEvaluators(Map<Integer, ExpressionEvaluator> evaluators) {
        this.evaluators.putAll(evaluators);
    }

    public String expand(String value) throws ExpressionExpansionException {
        StringBuilder builder = new StringBuilder();
        expand(value, 0, builder);
        return builder.toString();
    }

    /**
     * Recursively expands expressions contained in the given string starting at the supplied index.e
     *
     * @param value   the string containing the expressions to expand
     * @param index   the starting index
     * @param builder the builder to expand expressions to
     * @return an updated builder with the expanded expressions
     * @throws ExpressionExpansionException if an invalid expression is found  or a value does not exist for the expression, i.e. it is undefined in
     *                                      the ExpressionEvaluator's data set
     */
    private StringBuilder expand(String value, int index, StringBuilder builder) throws ExpressionExpansionException {
        
        if (value == null) {
            return builder;
        }
        
        int start = value.indexOf(PREFIX, index);
        if (start == -1) {
            return builder.append(value.substring(index));
        }
        int end = value.indexOf(POSTFIX, index + 2);
        if (end == -1) {
            throw new ExpressionExpansionException("No closing " + POSTFIX + " for expression starting at " + start + " in :" + value);
        }
        if (index != start) {
            builder.append(value.substring(index, start));
        }
        // expand the expression
        String expression = value.substring(start + 2, end);
        String evaluated = evaluate(expression);
        if (evaluated == null) {
            throw new ValueNotFoundException("Value not defined for '" + expression + "' in: " + value);
        }
        builder.append(evaluated);

        if (end < value.length() - 1) {
            expand(value, end + 1, builder);
        }
        return builder;
    }

    /**
     * Iterates through the ExpressionEvaluators until the given expression is evaluated.
     *
     * @param expression the expression to evaluate
     * @return the expanded expression or null if no value can be sourced
     */
    private String evaluate(String expression) {
        for (ExpressionEvaluator evaluator : evaluators.values()) {
            String expanded = evaluator.evaluate(expression);
            if (expanded != null) {
                return expanded;
            }
        }
        return null;
    }


}
