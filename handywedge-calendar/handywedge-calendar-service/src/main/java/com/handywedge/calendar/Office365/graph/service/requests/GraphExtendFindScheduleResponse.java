package com.handywedge.calendar.Office365.graph.service.requests;

import com.handywedge.calendar.Office365.rest.models.ScheduleDetailItem;

import java.util.List;

public class GraphExtendFindScheduleResponse extends BaseResponse{
    public List<ScheduleDetailItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(List<ScheduleDetailItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    List<ScheduleDetailItem> scheduleItems;
}
