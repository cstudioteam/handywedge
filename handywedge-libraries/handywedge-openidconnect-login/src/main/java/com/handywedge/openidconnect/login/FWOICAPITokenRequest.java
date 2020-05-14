/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.handywedge.rest.FWRESTRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FWOICAPITokenRequest extends FWRESTRequest {

  private String id;
  private String name;
  private String mail_address;
  private String provider;

}
