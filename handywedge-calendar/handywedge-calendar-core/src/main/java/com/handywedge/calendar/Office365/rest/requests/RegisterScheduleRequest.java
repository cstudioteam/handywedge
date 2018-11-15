
package com.handywedge.calendar.Office365.rest.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handywedge.calendar.Office365.rest.models.FreeBusyStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 予定表登録 リクエストモデル
 */
public class RegisterScheduleRequest {
  @JsonProperty("organizer")
  private String organizer;

  @JsonProperty("attendees")
  private List<String> attendees = new ArrayList<String>();

  @JsonProperty("subject")
  private String subject;

  @JsonProperty("body")
  private String body;

  @JsonProperty("startTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
  private String startTime;

  @JsonProperty("endTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
  private String endTime;

  @JsonProperty("locations")
  private List<String> locations;

  @JsonProperty("status")
  private FreeBusyStatusEnum status = FreeBusyStatusEnum.busy;

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

  public FreeBusyStatusEnum getStatus() {
    return status;
  }

  public void setStatus(FreeBusyStatusEnum status) {
    this.status = status;
  }

}
