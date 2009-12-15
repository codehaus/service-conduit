package com.travelex.tgbp.schema.model.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/schemaModel")
public interface SchemaModelBuilder {

    @GET
    @Path("/show.htm")
    String getSchemaDataModel(@QueryParam("schema")String schemaName);

}
