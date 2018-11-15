package com.handywedge.calendar.Office365.rest.annotations;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeParameterConverter implements ParamConverter<LocalDateTime> {

    @Override
    public LocalDateTime fromString(String string) {
        if (string != null) {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            return null;
        }
    }

    @Override
    public String toString(LocalDateTime dateTime) {
        return dateTime.format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
    }
}
