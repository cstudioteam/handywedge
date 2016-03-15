/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest;

/**
 * エラー応答するためのJSONクラスです。
 */
public class FWRESTErrorResponse extends FWRESTResponse {

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("FWRESTErrorResponse [getReturnCd()=").append(getReturn_cd())
        .append(", getReturnMsg()=").append(getReturn_msg()).append("]");
    return builder.toString();
  }

}
