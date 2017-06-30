/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest.api.token;

import com.handywedge.rest.FWRESTResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FWAPITokenResponse extends FWRESTResponse {

  private String token;

  @Override
  public String toString() {
    return "FWAPITokenResponse [token=" + token + ", getReturn_cd()=" + getReturn_cd()
        + ", getReturn_msg()=" + getReturn_msg() + "]";
  }

}
