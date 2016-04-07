/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest.api.user;

import com.csframe.rest.FWRESTRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class FWUserManagerRequest extends FWRESTRequest {

  /*
   * パスワード変更
   */
  private String current_password;
  private String new_password;

  /*
   * ユーザー登録
   */
  private String id;
  private String password;

}
