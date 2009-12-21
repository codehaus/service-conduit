package com.travelex.tgbp.rules.dynamic.execute.api;

public class DynamicRuleException extends RuntimeException {

    public DynamicRuleException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DynamicRuleException(String arg0) {
        super(arg0);
    }

}
