package com.travelex.tgbp.output.timing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Service to invoke output processes.
 */
@Path("")
public interface OutputServiceWrapper {

    @GET
    @Path("/startOutput.do")
    String startOutputService(@QueryParam("msg") String message);

}
