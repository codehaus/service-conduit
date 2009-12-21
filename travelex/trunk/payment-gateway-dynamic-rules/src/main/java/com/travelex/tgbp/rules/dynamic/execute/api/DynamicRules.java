package com.travelex.tgbp.rules.dynamic.execute.api;

import com.travelex.tgbp.store.domain.Instruction;

public interface DynamicRules {

    RoutingDecision getRouting(String schemaName, Instruction instruction);


}
