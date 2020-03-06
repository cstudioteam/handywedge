package com.handywedge.calendar.Office365.rest.exception;

public class CalendarApiException extends Exception{
    /**
     * エラーコード
     */
    private int statusCode;

    public CalendarApiException() { }

    public CalendarApiException(int code, String message) {
        super(message);
        this.statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

}
