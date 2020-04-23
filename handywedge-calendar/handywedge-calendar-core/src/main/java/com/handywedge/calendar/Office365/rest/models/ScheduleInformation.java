
package com.handywedge.calendar.Office365.rest.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;


/**
 * 予定表情報モデル
 */
public class ScheduleInformation {

  @JsonProperty("scheduleId")
  @NotNull
  private String scheduleId = "";

  @JsonProperty("hasError")
  private Boolean hasError = false;

  @JsonProperty("errorCode")
  private String errorCode = "";

  @JsonProperty("scheduleSummaryItems")
  private List<ScheduleSummaryItem> scheduleSummaryItems = new ArrayList<ScheduleSummaryItem>();

  @JsonIgnore
  private Long headerRetryTime = Long.valueOf(0);

  @JsonIgnore
  public Long getHeaderRetryTime() {
    return headerRetryTime;
  }

  public void setHeaderRetryTime(Long headerRetryTime) {
    this.headerRetryTime = headerRetryTime;
  }


  public String getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(String scheduleId) {
    this.scheduleId = scheduleId;
  }

  public List<ScheduleSummaryItem> getScheduleSummaryItems() {
    return scheduleSummaryItems;
  }

  public void setScheduleSummaryItems(List<ScheduleSummaryItem> scheduleSummaryItems) {
    this.scheduleSummaryItems = scheduleSummaryItems;
  }

  public Boolean getHasError() {
    return hasError;
  }

  public void setHasError(Boolean hasError) {
    this.hasError = hasError;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }
}
