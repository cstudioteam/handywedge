/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user;

import java.sql.Timestamp;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import lombok.Data;

@Data
@SessionScoped
public class FWUserImpl implements FWFullUser {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private Locale locale;
  private Timestamp lastLoginTime;

  @PostConstruct
  public void init() {
    locale = Locale.getDefault();
  }

}
