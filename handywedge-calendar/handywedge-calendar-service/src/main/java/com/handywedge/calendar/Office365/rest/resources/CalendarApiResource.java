package com.handywedge.calendar.Office365.rest.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.rest.exception.CalendarApiException;
import com.handywedge.calendar.Office365.rest.injections.CalendarApiConfig;
import com.handywedge.calendar.Office365.rest.injections.CalendarApiService;
import com.handywedge.calendar.Office365.rest.requests.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Path("/o365/calendar")
@RequestScoped
public class CalendarApiResource {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    CalendarApiService apiService;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @POST
    @Path("/getSchedule")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedule(@Valid GetScheduleRequest request){
        LocalDateTime startTime = LocalDateTime.now();
        logger.info( "getSchedule Start." );
        logger.debug( "Request: {}", gson.toJson( request ));

        GetScheduleResponse response = new GetScheduleResponse();

        if(ObjectUtils.isEmpty(request.getEmails())){
            logger.info("予定表取得の依頼件数が０件です。");
            return Response.ok(response.getScheduleInformation()).build();
        }

        try {
            apiService.getSchedule(request, response );
        }catch (GraphApiException gae) {
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR).entity( gae ).build();
        }catch (Exception e){
            logger.warn(e.getMessage(), e);
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( "ServerError" ).build();
        }

        logger.debug( "Response: {}", gson.toJson( response ));
        LocalDateTime endTime = LocalDateTime.now();
        logger.debug("{} 処理時間：{}ms","[予定表取得処理]", ChronoUnit.MILLIS.between(startTime, endTime));
        return Response.ok(response.getScheduleInformation()).build();
    }

}
