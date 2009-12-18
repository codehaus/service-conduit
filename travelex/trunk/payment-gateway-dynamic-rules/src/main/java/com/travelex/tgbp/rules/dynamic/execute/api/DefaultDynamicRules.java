package com.travelex.tgbp.rules.dynamic.execute.api;

import java.util.List;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.domain.rule.DynamicRule;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class DefaultDynamicRules implements DynamicRules {

    @Reference DataStore dataStore;

    @Override
    public RoutingDecision getRouting(String schemaName, Instruction instruction) {

        List<DynamicRule> execute = dataStore.execute(Query.DYNAMIC_RULE_LOOKUP, schemaName);

        return null;
    }

}
