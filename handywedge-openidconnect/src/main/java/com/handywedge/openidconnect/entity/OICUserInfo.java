/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * ユーザ情報を保持するクラス。
 *
 * @author takeuchi
 */
// 今後OICKeyやAzureOICProviderMetadataのようにAPIの変更でプロパティが増えるかもしれないので一律追加
@JsonIgnoreProperties(ignoreUnknown = true)
public class OICUserInfo {

  private String id;
  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
