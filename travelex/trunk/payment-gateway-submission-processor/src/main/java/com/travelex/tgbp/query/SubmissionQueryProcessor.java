package com.travelex.tgbp.query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("")
public interface SubmissionQueryProcessor {

    @GET
    @Path("/querySubmissions.htm")
    String getInstructionDataByMsgId(@QueryParam("messageId") String messageId);

}
