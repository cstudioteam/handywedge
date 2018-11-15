package com.handywedge.calendar.Office365.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 予定表削除API リクエストモデル
 */

public class DeleteScheduleRequest {
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
