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
 * Jwks JW Keyを保持するクラス。
 *
 * @author takeuchi
 */
// 今後OICKeyやAzureOICProviderMetadataのようにAPIの変更でプロパティが増えるかもしれないので一律追加
@JsonIgnoreProperties(ignoreUnknown = true)
public class OICJwks {

  private OICKey[] keys;

  public OICKey[] getKeys() {
    return keys;
  }

  public void setKeys(OICKey[] keys) {
    this.keys = keys;
  }

}
