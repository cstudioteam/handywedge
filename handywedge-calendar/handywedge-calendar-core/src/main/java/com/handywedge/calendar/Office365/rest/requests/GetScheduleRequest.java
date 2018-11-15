package com.handywedge.calendar.Office365.rest.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handywedge.calendar.Office365.rest.models.FreeBusyStatusEnum;


import java.util.ArrayList;
import java.util.List;

/**
 * 予定表取得API リクエストモデル
 */

public class GetScheduleRequest {
  @JsonProperty("emails")
  private List<String> emails = new ArrayList<String>();

  @JsonProperty("startTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
  private String startTime;

  @JsonProperty("endTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
  private String endTime;

  @JsonProperty("statuses")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private List<FreeBusyStatusEnum> statuses;

  public List<FreeBusyStatusEnum> getStatuses() {
        return statuses;
    }

  public void setStatuses(List<FreeBusyStatusEnum> statuses) {
        this.statuses = statuses;
    }

  public List<String> getEmails() {
    return emails;
  }

  public void setEmails(List<String> emails) {
    this.emails = emails;
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
