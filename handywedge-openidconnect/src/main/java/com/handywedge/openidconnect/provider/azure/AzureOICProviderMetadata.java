package com.handywedge.openidconnect.provider.azure;

import com.handywedge.openidconnect.entity.OICProviderMetadata;

/**
 * Azure特有の情報がある場合、com.handywedge.openidconnect.OICProviderMetadataをオーバーライドする。
 * 
 * @author takeuchi
 */
public class AzureOICProviderMetadata extends OICProviderMetadata {
    
    private String microsoft_multi_refresh_token;
    private String msgraph_host;

    public String getMicrosoft_multi_refresh_token() {
        return microsoft_multi_refresh_token;
    }

    public void setMicrosoft_multi_refresh_token(String microsoft_multi_refresh_token) {
        this.microsoft_multi_refresh_token = microsoft_multi_refresh_token;
    }

    public String getMsgraph_host() {
        return msgraph_host;
    }

    public void setMsgraph_host(String msgraph_host) {
        this.msgraph_host = msgraph_host;
    }

}
