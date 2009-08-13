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

import java.util.Map;
import java.util.TreeMap;

import org.osoa.sca.annotations.Reference;

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
