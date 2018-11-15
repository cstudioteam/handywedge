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

public class FindScheduleByPeriodRequest {
  @JsonProperty("email")
  private String email = "";

  @JsonProperty("startTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
  private String startTime;

  @JsonProperty("endTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddThh:mm:ss")
  private String endTime;

  @JsonProperty("status")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @JsonIgnore
  private FreeBusyStatusEnum status = FreeBusyStatusEnum.busy;

  public FreeBusyStatusEnum getStatus() {
        return status;
    }

  public void setStatus(FreeBusyStatusEnum status) {
        this.status = status;
    }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
