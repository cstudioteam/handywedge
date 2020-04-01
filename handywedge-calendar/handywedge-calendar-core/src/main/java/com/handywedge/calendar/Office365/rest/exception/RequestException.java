package com.handywedge.calendar.Office365.rest.exception;

public class RequestException extends Exception{
    /**
     * エラーコード
     */
    private int code;

    /**
     * エラー項目
     */
    private String fields;

    /**
     * 拡張メッセージ
     */
    private String exMessage;


    public RequestException() { }

    public RequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getExMessage() {
        return exMessage;
    }

    public void setExMessage(String exMessage) {
        this.exMessage = exMessage;
    }

}
