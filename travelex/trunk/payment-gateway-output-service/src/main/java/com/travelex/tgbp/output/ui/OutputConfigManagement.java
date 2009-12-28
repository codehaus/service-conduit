package com.travelex.tgbp.output.ui;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Service to view/update output configuration parameters.
 */
@Path("")
public interface OutputConfigManagement {

    @GET
    @Path("/getConfigOnCharge.do")
    String getConfigOnCharge();

    @GET
    @Path("/updateConfigOnCharge.do")
    String updateConfigOnCharge(@QueryParam("id") String id, @QueryParam("charge")String charge, @QueryParam("amount")String thresholdAmount);

    @GET
    @Path("/getDynamicRules.do")
    String getDynamicRules();

    @GET
    @Path("/deleteDynamicRule.do")
    String deleteDynamicRule(@QueryParam("id") String id);
}
