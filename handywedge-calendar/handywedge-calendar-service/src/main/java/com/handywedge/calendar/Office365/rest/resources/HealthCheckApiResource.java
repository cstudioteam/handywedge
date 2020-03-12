package com.handywedge.calendar.Office365.rest.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("")
public class HealthCheckApiResource {

    private static final Logger logger = LogManager.getLogger();

    @GET
    public Response get(@Context UriInfo uriInfo, @Context HttpServletRequest req){
        logger.debug( "HealthCheck Get Method Api." );
        return Response.ok().build();
    }

    @POST
    public Response post(@Context UriInfo uriInfo, @Context HttpServletRequest req){
        logger.debug( "HealthCheck Post Method Api." );
        return Response.ok().build();
    }

}
