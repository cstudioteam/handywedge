package com.handywedge.calendar.Office365.graph.service.extension;

import com.microsoft.graph.content.MSBatchRequestStep;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

public class ExtendBatchRequestStep extends MSBatchRequestStep {
    List<String> requestUsers = new ArrayList<>(  );

    public ExtendBatchRequestStep(String requestId, Request request, List<String> arrayOfDependsOnIds, List<String> requestUsers) {
        super( requestId, request, arrayOfDependsOnIds );
        this.requestUsers = requestUsers;
    }

    public List<String> getRequestUsers(){
        return this.requestUsers;
    }
}
