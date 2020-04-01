
package com.handywedge.calendar.Office365.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.handywedge.calendar.Office365.rest.BaseResponse;
import com.handywedge.calendar.Office365.rest.models.ScheduleInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 予定表取得レスポンスモデル
 */

public class GetScheduleResponse extends BaseResponse {

  @JsonProperty("scheduleInformation")
  private List<ScheduleInformation> scheduleInformation = new ArrayList<ScheduleInformation>( );

  public List<ScheduleInformation> getScheduleInformation() {
    return scheduleInformation;
  }

  public void setScheduleInformation(List<ScheduleInformation> scheduleInformation) {
    this.scheduleInformation = scheduleInformation;
  }

}
