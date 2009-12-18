package com.travelex.tgbp.store.service.api;

public enum Query {

    DYNAMIC_RULE_LOOKUP("GET_D_RULES_BY_SCHEMA");

    private String jpaName;

    private Query(String jpaName) {
        this.jpaName = jpaName;
    }

    public String getJpaName() {
        return jpaName;
    }

}
