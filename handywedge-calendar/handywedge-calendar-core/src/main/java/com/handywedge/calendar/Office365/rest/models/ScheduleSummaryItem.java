package com.handywedge.calendar.Office365.rest.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ScheduleSummaryItem {

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("status")
    private FreeBusyStatusEnum status;

    @JsonProperty("startTime")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
    private String startTime;

    @JsonProperty("endTime")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
    private String endTime;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public FreeBusyStatusEnum getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = FreeBusyStatusEnum.fromValue( status );
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

}
