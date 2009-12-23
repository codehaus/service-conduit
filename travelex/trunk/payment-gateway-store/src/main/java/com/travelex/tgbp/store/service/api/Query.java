package com.travelex.tgbp.store.service.api;

public enum Query {

    DYNAMIC_RULE_LOOKUP("GET_D_RULES_BY_SCHEMA"),

    SUBMISSION_INSTRUCTION_COUNT("GET_SUBMISSION_INSTRUCTION_COUNT"),

    SUBMISSION_COUNT_BY_DATE("GET_SUBMISSION_COUNT_BY_ARRIVAL_DATE"),

    INSTRUCTION_TOTALS_BY_DATE("GET_VALUES_BY_ARRIVAL_DATE");

    private String jpaName;

    private Query(String jpaName) {
        this.jpaName = jpaName;
    }

    public String getJpaName() {
        return jpaName;
    }

}
