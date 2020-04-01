package com.handywedge.calendar.Office365.graph.service.config;


import java.net.Proxy.Type;

public class GraphProxyInfo {
    private Type type = Type.HTTP;

    private String host = "";

    private int port = 0;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
