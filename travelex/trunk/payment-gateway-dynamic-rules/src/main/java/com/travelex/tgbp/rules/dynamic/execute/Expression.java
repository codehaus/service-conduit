package com.travelex.tgbp.rules.dynamic.execute;

public class Expression {

    private final String xPath;
    private final Operator operator;
    private final String javaType;
    private final String ruleData;

    public Expression(String xPath, String operator, String javaType, String ruleData) {
        this.xPath = xPath;
        this.operator = Operator.getByLabel(operator);
        this.javaType = javaType;
        this.ruleData = ruleData;
    }

    public String getXPath() {
        return xPath;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getRuleData() {
        return ruleData;
    }

}
