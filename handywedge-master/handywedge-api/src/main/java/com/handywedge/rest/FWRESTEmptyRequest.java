/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 何も属性を持たないJSONリクエストクラスです。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FWRESTEmptyRequest extends FWRESTRequest {

  @Override
  public String toString() {
    return "FWRESTEmptyRequest class.";
  }

}
