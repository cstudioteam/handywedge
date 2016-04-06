/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * ユーザー情報を更新するときの引数となるビーンクラスです。
 */
@ToString
@Data
@AllArgsConstructor
public class FWUserData {

  private String id;
  private String name;
  private String role;
  private Locale locale;

  public FWUserData() {}

  public FWUserData(FWUser user) {
    id = user.getId();
    name = user.getName();
    role = user.getRole();
    locale = user.getLocale();
  }

}
