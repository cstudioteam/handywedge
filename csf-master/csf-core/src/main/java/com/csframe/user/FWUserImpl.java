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
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@SessionScoped
public class FWUserImpl extends FWUserData implements FWFullUser {

  private static final long serialVersionUID = 1L;

  private Timestamp lastLoginTime;

  @PostConstruct
  public void init() {
    super.setLocale(Locale.getDefault());
  }

  @Override
  public String toString() {
    return "FWUserImpl [lastLoginTime=" + lastLoginTime + ", toString()=" + super.toString() + "]";
  }

}
