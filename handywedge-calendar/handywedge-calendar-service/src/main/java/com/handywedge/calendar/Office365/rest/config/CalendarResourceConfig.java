package com.handywedge.calendar.Office365.rest.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.handywedge.calendar.Office365.rest.exception.JsonParseExceptionMapper;
import com.handywedge.calendar.Office365.rest.injections.CalendarApiConfig;
import com.handywedge.calendar.Office365.rest.injections.CalendarApiService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/v1")
public class CalendarResourceConfig extends ResourceConfig {
    public CalendarResourceConfig() {
        // Jersey Bean Validation
        packages("com.baeldung.jersey.server");
        property( ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        register( JacksonJaxbJsonProvider.class);
        register( JsonParseExceptionMapper.class, 1);
//        register( JacksonFeature.class );

        register( CalendarApiService.class );
        register( new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract( CalendarApiService.class ).in( RequestScoped.class );
                bindAsContract( CalendarApiConfig.class ).in( RequestScoped.class );
            }
        } );
    }
}