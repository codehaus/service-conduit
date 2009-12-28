package com.travelex.tgbp.output.ui;

import java.math.BigDecimal;
import java.util.List;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.routing.RoutingConfigOnValueDate;
import com.travelex.tgbp.store.domain.rule.DynamicRule;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

/**
 * Default implementation for {@link OutputConfigManagement}
 */
public class DefaultOutputConfigManagement implements OutputConfigManagement {

    @Reference protected DataStore dataStore;

    /**
     * {@inheritDoc}
     */
    public String getConfigOnCharge() {
        List<RoutingConfigOnValueDate> configList = dataStore.execute(Query.GET_ALL_CONFIG_ON_VALUE_DATE);
        return prepareValueConfigResultXml(configList);
    }

    /**
     * {@inheritDoc}
     */
    public String updateConfigOnCharge(String id, String charge, String thresholdAmount) {
        dataStore.update(Query.UPDATE_VALUE_DATE_CONFIG_BY_ID, new BigDecimal(thresholdAmount), Double.valueOf(charge), Long.valueOf(id));
        return "success";
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicRules() {
        List<DynamicRule> dynamicRules = dataStore.execute(Query.GET_ALL_DYNAMIC_RULES);
        return prepareDynamicRuleResultXml(dynamicRules);
    }

    /**
     * {@inheritDoc}
     */
    public String deleteDynamicRule(String id) {
        dataStore.update(Query.DELETE_DYNAMIC_RULE_BY_ID, Long.valueOf(id));
        return "success";
    }

    private String prepareValueConfigResultXml(List<RoutingConfigOnValueDate> configList) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Data>");
        for (RoutingConfigOnValueDate config : configList) {
             sb.append("<rule>");
             sb.append("<id>");sb.append(config.getKey().toString());sb.append("</id>");
             sb.append("<curr>");sb.append(config.getCurrency().name());sb.append("</curr>");
             sb.append("<scheme>");sb.append(config.getClearingMechanism().name());sb.append("</scheme>");
             sb.append("<amount>");sb.append(config.getThresholdAmount().toPlainString());sb.append("</amount>");
             sb.append("<period>");sb.append(config.getClearingPeriod());sb.append("</period>");
             sb.append("<charge>");sb.append(config.getCharge());sb.append("</charge>");
             sb.append("</rule>");
        }
        sb.append("</Data>");
        return sb.toString();
    }

    private String prepareDynamicRuleResultXml(List<DynamicRule> dynamicRules) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Data>");
         for (DynamicRule rule : dynamicRules) {
             sb.append("<rule>");
             sb.append("<id>");sb.append(rule.getKey());sb.append("</id>");
             sb.append("<scheme>");sb.append(rule.getClearingMechanism());sb.append("</scheme>");
             sb.append("<name>");sb.append(rule.getRuleName());sb.append("</name>");
             sb.append("<schema>");sb.append(rule.getSchema());sb.append("</schema>");
             sb.append("</rule>");
         }
        sb.append("</Data>");
        return sb.toString();
    }

}
