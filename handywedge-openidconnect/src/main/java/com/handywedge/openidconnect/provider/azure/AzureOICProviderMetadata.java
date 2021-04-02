/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect.provider.azure;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.handywedge.openidconnect.entity.OICProviderMetadata;

/**
 * Azure特有の情報がある場合、com.handywedge.openidconnect.OICProviderMetadataをオーバーライドする。
 *
 * @author takeuchi
 */
// FIXME 暫定対応で追加されたメタデータ（device_authorization_endpoint）のデシリアライズを無視
@JsonIgnoreProperties(ignoreUnknown = true)
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
