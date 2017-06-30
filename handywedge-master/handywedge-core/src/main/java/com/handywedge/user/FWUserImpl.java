/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user;

import java.sql.Timestamp;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import com.handywedge.user.FWFullUser;
import com.handywedge.user.FWUserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@SessionScoped
public class FWUserImpl extends FWUserData implements FWFullUser {

  private static final long serialVersionUID = 1L;

  private Timestamp lastLoginTime;

  private Timestamp beforeLoginTime;

  @PostConstruct
  public void init() {
    super.setLocale(Locale.getDefault());
  }

  @Override
  public String toString() {
    return "FWUserImpl [lastLoginTime=" + lastLoginTime + ", beforeLoginTime=" + beforeLoginTime
        + ", toString()=" + super.toString() + "]";
  }

}
