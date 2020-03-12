package com.handywedge.calendar.Office365.rest.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.graph.exceptions.GraphRequestTimeoutException;
import com.handywedge.calendar.Office365.rest.models.FreeBusyStatusEnum;
import com.handywedge.calendar.Office365.rest.requests.*;
import com.handywedge.calendar.Office365.rest.exception.CalendarApiException;
import com.handywedge.calendar.Office365.rest.injections.CalendarApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Path("/o365/schedule")
public class ScheduleApiResource {

    private static final Logger logger = LogManager.getLogger();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Inject
    CalendarApiService apiService;

    @DELETE
    @Path("/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSchedule(
            @NotEmpty(message = "immutableId must not be null") @QueryParam("immutableId") String immutableId,
            @NotEmpty(message = "email must not be null") @QueryParam("email") String email
    ) throws CalendarApiException {
        LocalDateTime timeStart = LocalDateTime.now();

        DeleteScheduleResponse response = null;
        DeleteScheduleRequest request = new DeleteScheduleRequest();
        request.setId( immutableId );
        request.setOrganizer( email );

        try {
            apiService.deleteSchedule( request, response );
        }catch (GraphApiException gae) {
            gae.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR).entity( gae ).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( e.getMessage() ).build();
        }

        logger.debug( "Response: {}", gson.toJson( response ));
        LocalDateTime timeEnd = LocalDateTime.now();
        logger.debug("{} 処理時間：{}ms","[予定表取消処理]", ChronoUnit.MILLIS.between(timeStart, timeEnd));
        return Response.noContent().build();
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSchedule(
            @Valid RegisterScheduleRequest request
    ) throws CalendarApiException {
        LocalDateTime timeStart = LocalDateTime.now();

        RegisterScheduleResponse response = new RegisterScheduleResponse();
        logger.debug( "Request: {}", gson.toJson( request ));
        try {
            apiService.registerSchedule(request, response);
        }catch (GraphRequestTimeoutException grte){
            return Response.status( Response.Status.REQUEST_TIMEOUT).build();
        }catch (GraphApiException gae) {
            gae.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR).entity( gae ).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( "ServerError" ).build();
        }

        logger.debug( "Response: {}", gson.toJson( response ));
        LocalDateTime timeEnd = LocalDateTime.now();
        logger.debug("{} 処理時間：{}ms","[予定表登録処理]", ChronoUnit.MILLIS.between(timeStart, timeEnd));
        return Response.ok(response).build();
    }

    @GET
    @Path("/findById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findSchedule(
            @NotEmpty(message = "immutableId must not be null") @QueryParam("immutableId") String immutableId,
            @NotEmpty(message = "email must not be null") @QueryParam("email") String email
    ) throws CalendarApiException {
        LocalDateTime timeStart = LocalDateTime.now();

        FindScheduleByIdResponse response = new FindScheduleByIdResponse();
        FindScheduleByIdRequest request = new FindScheduleByIdRequest();
        request.setId( immutableId );
        request.setOrganizer( email );
        try {
            apiService.findSchedule( request, response );
        }catch (GraphApiException gae) {
            gae.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR).entity( gae ).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( "ServerError" ).build();
        }

        logger.debug( "Response: {}", gson.toJson( response ));
        LocalDateTime timeEnd = LocalDateTime.now();
        logger.debug("{} 処理時間：{}ms","[予定表検索処理]", ChronoUnit.MILLIS.between(timeStart, timeEnd));
        return Response.ok(response).build();
    }

    @GET
    @Path("/findByPeriod")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findSchedule(
            @NotNull(message = "email must not be null") @QueryParam("email") String email,
            @NotNull(message = "startTime must not be null") @QueryParam("startTime") String startTime,
            @NotNull(message = "endTime must not be null") @QueryParam("endTime") String endTime,
            @NotNull(message = "status must not be null") @QueryParam("status") String status
    ) throws CalendarApiException {
        LocalDateTime timeStart = LocalDateTime.now();

        FindScheduleByPeriodResponse response = null;
        FindScheduleByPeriodRequest request = new FindScheduleByPeriodRequest();
        request.setEmail( email );
        request.setStartTime( startTime );
        request.setEndTime( endTime );
        request.setStatus( FreeBusyStatusEnum.fromValue( status ) );
        try {
            apiService.findSchedule( request, response );
        }catch (GraphApiException gae) {
            gae.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR).entity( gae ).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( "ServerError" ).build();
        }

        logger.debug( "Response: {}", gson.toJson( response ));
        LocalDateTime timeEnd = LocalDateTime.now();
        logger.debug("{} 処理時間：{}ms","[予定表検索処理]", ChronoUnit.MILLIS.between(timeStart, timeEnd));
        return Response.ok(response).build();
    }
}
