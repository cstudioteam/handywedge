package com.handywedge.calendar.Office365.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ScheduleDetailItem extends ScheduleSummaryItem {

    @JsonProperty("id")
    @NotNull
    private String id;

    @JsonProperty("organizer")
    private String organizer;

    @JsonProperty("attendees")
    private List<String> attendees;

    @JsonProperty("body")
    private String body;

    @JsonProperty("locations")
    private List<String> locations;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

}
