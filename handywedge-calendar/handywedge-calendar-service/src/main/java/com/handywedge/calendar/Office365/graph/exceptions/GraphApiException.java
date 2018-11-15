package com.handywedge.calendar.Office365.graph.exceptions;

public class GraphApiException extends Exception {

    private String code;

    public GraphApiException(String message) {
        super( message );
    }

    public GraphApiException(String code, String message) {
        super( message );
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
