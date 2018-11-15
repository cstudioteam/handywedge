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

public class FindScheduleByIdRequest {
  @JsonProperty("id")
  private String id = new String();

  @JsonProperty("organizer")
  private String organizer;

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
}
