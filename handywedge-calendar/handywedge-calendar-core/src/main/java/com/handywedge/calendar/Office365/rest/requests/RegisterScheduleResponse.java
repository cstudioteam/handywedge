
package com.handywedge.calendar.Office365.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 予定表登録レスポンスモデル
 */
public class RegisterScheduleResponse {

  @JsonProperty("immutableId")
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
