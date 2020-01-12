/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect.entity;

/**
 * Jwks JW Keyを保持するクラス。
 *
 * @author takeuchi
 */
public class OICJwks {

  private OICKey[] keys;

  public OICKey[] getKeys() {
    return keys;
  }

  public void setKeys(OICKey[] keys) {
    this.keys = keys;
  }

}
