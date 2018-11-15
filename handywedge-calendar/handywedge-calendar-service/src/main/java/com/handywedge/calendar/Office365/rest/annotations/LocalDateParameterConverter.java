package com.handywedge.calendar.Office365.rest.annotations;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateParameterConverter implements ParamConverter<LocalDate> {

    @Override
    public LocalDate fromString(String string) {
        if (string != null) {
            return LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            return null;
        }
    }

    @Override
    public String toString(LocalDate date) {
        return date.format( DateTimeFormatter.ISO_LOCAL_DATE );
    }
}
