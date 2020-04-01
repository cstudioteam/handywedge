package com.handywedge.calendar.Office365.graph.service.requests;


import java.util.List;

public class GraphExtendGetScheduleRequest extends BaseRequest {

    private List<String> schedules;
    private String startTime;
    private String endTime;
    private int availabilityViewInterval;

    public List<String> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<String> schedules) {
        this.schedules = schedules;
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

    public int getAvailabilityViewInterval() {
        return availabilityViewInterval;
    }

    public void setAvailabilityViewInterval(int availabilityViewInterval) {
        this.availabilityViewInterval = availabilityViewInterval;
    }

}
