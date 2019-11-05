package com.handywedge.calendar.Office365.graph.service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.handywedge.calendar.Office365.rest.models.FreeBusyStatusEnum;

import java.util.ArrayList;
import java.util.List;

public class GraphExtendRegisterScheduleRequest extends BaseRequest {

    private String organizer;
    private String subject;
    private String body;
    private List<String> attendees = new ArrayList<String>();
    private String startTime;
    private String endTime;
    private List<String> locations = new ArrayList<String>();

    private FreeBusyStatusEnum status = FreeBusyStatusEnum.busy;

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    public FreeBusyStatusEnum getStatus() {
        return status;
    }

    public void setStatus(FreeBusyStatusEnum status) {
        this.status = status;
    }

}
