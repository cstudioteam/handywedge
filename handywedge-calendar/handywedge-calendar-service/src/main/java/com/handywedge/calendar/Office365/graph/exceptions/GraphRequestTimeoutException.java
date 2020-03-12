package com.handywedge.calendar.Office365.graph.exceptions;

public class GraphRequestTimeoutException extends RuntimeException {
    public GraphRequestTimeoutException(String message) {
        super( message );
    }
}
