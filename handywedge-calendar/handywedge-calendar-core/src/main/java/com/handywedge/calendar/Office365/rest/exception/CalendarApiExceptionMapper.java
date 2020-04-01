package com.handywedge.calendar.Office365.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CalendarApiExceptionMapper implements ExceptionMapper<CalendarApiException> {
    public Response toResponse(CalendarApiException ex) {
        return Response.status( ex.getStatusCode())
                .entity(new ApiResponseMessage( ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
