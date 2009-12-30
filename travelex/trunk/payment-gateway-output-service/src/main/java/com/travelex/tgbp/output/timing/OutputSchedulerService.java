package com.travelex.tgbp.output.timing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Timing service to schedule output processes.
 */
@Path("")
public interface OutputSchedulerService {

    @GET
    @Path("/startOutputScheduler.do")
    String startOutputScheduler();

}
