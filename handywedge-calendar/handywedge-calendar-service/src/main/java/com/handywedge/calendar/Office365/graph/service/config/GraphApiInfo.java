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
     * Graph API　リクエストタイムアウト値
     */
    private  int requestTimeout = 15;

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

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }


}
