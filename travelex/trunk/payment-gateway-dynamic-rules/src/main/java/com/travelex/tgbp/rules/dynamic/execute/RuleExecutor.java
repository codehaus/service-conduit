package com.travelex.tgbp.rules.dynamic.execute;

import static com.travelex.tgbp.rules.dynamic.execute.Operator.EQUALS;
import static com.travelex.tgbp.rules.dynamic.execute.Operator.GREATER_THAN;
import static com.travelex.tgbp.rules.dynamic.execute.Operator.LESS_THAN;
import static com.travelex.tgbp.rules.dynamic.execute.Operator.NOT_EQUALS;

import java.lang.reflect.Constructor;

import com.travelex.tgbp.rules.dynamic.execute.api.DynamicRuleException;

public class RuleExecutor {

    @SuppressWarnings("unchecked")
    public static boolean passes(Object v1, Object v2, Operator operator, String javaType)  {

        int result = 0;
        try {
            Class<?> clazz = Class.forName(javaType);
            Constructor<?> constructor = clazz.getConstructor(String.class);
            if(!Comparable.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Unsupported type");
            }

            Comparable i1 = (Comparable)constructor.newInstance(v1);
            Object i2 = constructor.newInstance(v2);
            result = i1.compareTo(i2);
        }
        catch(Exception e) {
            throw new DynamicRuleException("Couldn't evaluate rule", e);
        }

        if(result == 0 && EQUALS == operator) {
            return true;
        }

        if(result == 0 && NOT_EQUALS == operator) {
            return false;
        }

        if(result < 0 && LESS_THAN == operator) {
            return true;
        }

        if(result > 0 && GREATER_THAN == operator) {
            return true;
        }

        return false;
    }
}
