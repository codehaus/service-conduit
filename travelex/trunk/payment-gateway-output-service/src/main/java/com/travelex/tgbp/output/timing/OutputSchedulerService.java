package com.travelex.tgbp.output.timing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Service to schedule output processes.
 */
@Path("")
public interface OutputSchedulerService {

    @GET
    @Path("/startOutput.do")
    String startOutputService();

}
