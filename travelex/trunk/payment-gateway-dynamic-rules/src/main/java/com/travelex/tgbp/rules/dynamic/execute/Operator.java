package com.travelex.tgbp.rules.dynamic.execute;

public enum Operator {

    EQUALS("Equals"),

    NOT_EQUALS("Not Equals"),

    LESS_THAN("Less Than"),

    GREATER_THAN("Greater Than");

    private String label;

    private Operator(String label) {
        this.label = label;
    }

    public static Operator getByLabel(String label) {
        Operator result = null;
        for (Operator operator: Operator.values()) {
            if(operator.label.equals(label)) {
                result = operator;
                break;
            }
        }

        return result;
    }

}
