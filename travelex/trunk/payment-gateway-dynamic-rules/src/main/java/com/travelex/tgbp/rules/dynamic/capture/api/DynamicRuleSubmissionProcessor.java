package com.travelex.tgbp.rules.dynamic.capture.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/rules")
public interface DynamicRuleSubmissionProcessor {

    //The return value isn't required but the binding blows up with void return type methods
    //Have to use get because form data isn't read properly by the binding
    @GET //@PUT NOTE: There is a typo in the http binding: uses PUT where it should use POST. This is the workaround!
    @Path("/create.htm")
    String createRule(@QueryParam("rulename")String ruleName,
            @QueryParam("clearingmechanism")String clearingMechanism,
            @QueryParam("ruledata")String ruleData,
            @QueryParam("appliesto")String appliesTo);



}
