package com.travelex.tgbp.routing.impl;

import java.text.MessageFormat;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.routing.service.ClearingMechanismResolver;
import com.travelex.tgbp.rules.dynamic.execute.api.DynamicRules;
import com.travelex.tgbp.rules.dynamic.execute.api.RoutingDecision;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.type.ClearingMechanism;

public class DynamicRuleResolver implements ClearingMechanismResolver {

    @Reference protected DynamicRules dynamicRules;

    @Override
    public ClearingMechanism resolve(Instruction instruction) {
        ClearingMechanism resolved = null;
        RoutingDecision routingDecision = dynamicRules.getRouting("pain.001.001.03", instruction);
        if(routingDecision.routeSpecified()) {
            log(routingDecision);
            resolved = Enum.valueOf(ClearingMechanism.class, routingDecision.getClearingMechanism());
        }

        return resolved;
    }

    private void log(RoutingDecision routingDecision) {
        String log = MessageFormat.format("Dynamic routing rule {0} passed. Routing to {1}",
                routingDecision.getRuleName(),
                routingDecision.getClearingMechanism());
        System.out.println(log);
    }

}
