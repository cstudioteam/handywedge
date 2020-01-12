/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest;

/**
 * 共通属性以外を持たないJSONレスポンスクラスです。
 */
public class FWRESTEmptyResponse extends FWRESTResponse {

  @Override
  public String toString() {
    return "FWRESTEmptyResponse [getReturn_cd()=" + getReturn_cd() + ", getReturn_msg()="
        + getReturn_msg() + "]";
  }

}
