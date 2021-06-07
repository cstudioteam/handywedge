/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user;

import java.io.Serializable;
import java.util.Locale;

import jakarta.enterprise.context.SessionScoped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ユーザー情報を更新するときの引数となるビーンクラスです。
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@SessionScoped
public class FWUserData implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String role;
  private String roleName;
  private Locale locale;

  public FWUserData() {}

  public FWUserData(FWUser user) {
    id = user.getId();
    name = user.getName();
    role = user.getRole();
    roleName = user.getRoleName();
    locale = user.getLocale();
  }

}
