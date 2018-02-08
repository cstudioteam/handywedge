/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest.api.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.handywedge.rest.FWRESTRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class FWAPITokenRequest extends FWRESTRequest {

  private String id;
  private String password;
  private Integer multiple;

}
