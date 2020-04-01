package com.handywedge.calendar.Office365.graph.service.requests;

import com.handywedge.calendar.Office365.graph.service.extension.ExtendBatchRequestStep;
import com.microsoft.graph.content.MSBatchRequestStep;

import java.util.ArrayList;
import java.util.List;

public class GraphExtendBatchRequest {
    List<MSBatchRequestStep> requestSteps = new ArrayList<>( );

    public List<MSBatchRequestStep> getRequestSteps() {
        return requestSteps;
    }

    public void setRequestSteps(List<ExtendBatchRequestStep> requestSteps) {
        for(MSBatchRequestStep newRequestSteps:requestSteps){
            this.requestSteps.add( newRequestSteps );
        }
    }
}
