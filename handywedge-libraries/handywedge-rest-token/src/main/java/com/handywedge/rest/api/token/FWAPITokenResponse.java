/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest.api.token;

import com.handywedge.rest.FWRESTResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FWAPITokenResponse extends FWRESTResponse {

  private String token;
  private FWAPITokenUser user = new FWAPITokenUser();

  @Override
  public String toString() {
    return "FWAPITokenResponse [token=" + token + ", user=" + user + ", getReturn_cd()="
        + getReturn_cd() + ", getReturn_msg()=" + getReturn_msg() + "]";
  }

  @Setter
  @Getter
  @ToString
  public class FWAPITokenUser {
    private String id;
    private String name;
    private String role;
    private String roleName;
    private String locale;
  }

}
