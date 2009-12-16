package com.travelex.tgbp.rules.dynamic.capture.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/rules")
public interface DynamicRuleSubmissionProcessor {

    //The return value isn't required but the binding blows up with void return type methods
    @POST
    @Path("/create.htm")
    String createRule(@QueryParam("rulename")String ruleName,
            @QueryParam("clearingmechanism")String clearingMechanism,
            @QueryParam("ruledata")String ruleData);

}
