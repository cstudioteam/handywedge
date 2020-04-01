package com.handywedge.calendar.Office365.graph.service.config;

/**
 * Graph API利用情報
 */
public class GraphApiInfo {
    /**
     * タイムゾン
     */
    private String timeZone = "Tokyo Standard Time";

    /**
     * プロキシ利用有無
     */
    private boolean useProxy = false;

    /**
     *  Azure AD認証情報
     */
    private GraphAuthInfo authInfo;

    /**
     * Proxy情報
     */
    private GraphProxyInfo proxyInfo;

    /**
     * getSchedule APIのリクエスト毎の指定するユーザー数
     * 指定範囲：1〜２０
     */
    private int userNumber = 1;
    /**
     * getSchedule APIの同時発行のリクエスト数
     * 指定範囲：1〜２０
     */
    private int requestNumber = 1;

    /**
     * Graph API　バッチリクエストタイムアウト値(秒)
     */
    private  int requestTimeoutForBatch = 15;

    /**
     * Graph API　参照系リクエストタイムアウト値(秒)
     */
    private  int requestTimeoutForRead = 3;

    /**
     * Graph API　更新系リクエストタイムアウト値(秒)
     */
    private  int requestTimeoutForWrite = 5;

    /**
     * getSchedule APIの代理人ユーザー
     */
    private String delegate;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public GraphAuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(GraphAuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    public GraphProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(GraphProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getDelegate() {
        return delegate;
    }

    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }

    public int getRequestTimeoutForBatch() {
        return requestTimeoutForBatch;
    }

    public void setRequestTimeoutForBatch(int requestTimeoutForBatch) {
        this.requestTimeoutForBatch = requestTimeoutForBatch;
    }

    public int getRequestTimeoutForRead() {
        return requestTimeoutForRead;
    }

    public void setRequestTimeoutForRead(int requestTimeoutForRead) {
        this.requestTimeoutForRead = requestTimeoutForRead;
    }

    public int getRequestTimeoutForWrite() {
        return requestTimeoutForWrite;
    }

    public void setRequestTimeoutForWrite(int requestTimeoutForWrite) {
        this.requestTimeoutForWrite = requestTimeoutForWrite;
    }
}
