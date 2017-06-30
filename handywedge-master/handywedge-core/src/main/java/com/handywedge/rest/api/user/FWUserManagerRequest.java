/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest.api.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.handywedge.rest.FWRESTRequest;

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
  private Integer pre_register; // 仮登録フラグ
  private String mail_address;
  private String pre_token;

  /*
   * パスワード初期化
   */
  private Integer length;
}
