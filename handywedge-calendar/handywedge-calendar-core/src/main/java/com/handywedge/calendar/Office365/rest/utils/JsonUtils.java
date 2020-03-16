package com.handywedge.calendar.Office365.rest.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class JsonUtils {
    public static final String LOCAL_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    private static final Logger logger = LogManager.getLogger( );

    public static ObjectMapper buildMapper() {

        JavaTimeModule jtm = new JavaTimeModule();
        jtm.addSerializer( LocalDateTime.class, new LocalDateTimeSerializer( DateTimeFormatter.ofPattern(LOCAL_DATETIME_FORMAT)));
        jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATETIME_FORMAT)));
        jtm.addSerializer( LocalDate.class, new LocalDateSerializer( DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));
        jtm.addDeserializer( LocalDate.class, new LocalDateDeserializer( DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(jtm);

        mapper
                .enable( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable( DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable( DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable( SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    public static String objectToString(Object obj) {
        String objectStr = "";

        try {
            if (obj == null) {
                objectStr += String.format("%s", obj);
            }else if(obj.getClass() == LinkedHashMap.class){
                objectStr += obj.toString();
            } else if (obj.getClass().isArray()) {
                objectStr += Arrays.stream((Object[]) obj).map( elm -> objectToString(elm)).collect( Collectors.toList())
                        .toString();
            } else {
                objectStr += MessageFormat.format( "{0}", obj );
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        return objectStr;
    }

}
