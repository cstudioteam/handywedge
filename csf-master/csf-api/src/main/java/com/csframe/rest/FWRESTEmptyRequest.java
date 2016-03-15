/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 何も返さないJSONクラスです。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FWRESTEmptyRequest extends FWRESTRequest {

  @Override
  public String toString() {
    return "FWRESTEmptyRequest class.";
  }

}
