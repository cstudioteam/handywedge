package com.handywedge.calendar.Office365.graph.service.requests;

public class GraphExtendFindByIdScheduleRequest extends BaseRequest {
    private String id;
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
