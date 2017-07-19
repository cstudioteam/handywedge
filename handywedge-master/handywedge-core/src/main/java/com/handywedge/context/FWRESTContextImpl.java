/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@RequestScoped
public class FWRESTContextImpl implements FWFullRESTContext {

  @Getter
  @Setter
  private String userId;
  @Getter
  @Setter
  private String userName;
  @Getter
  @Setter
  private String userRole;
  @Getter
  @Setter
  private String userRoleName;
  @Getter
  @Setter
  private Locale userLocale;

  @Inject
  private FWRequestContext reqCtx;

  @Inject
  private FWApplicationContext appCtx;

  @PostConstruct
  public void init() {
    userLocale = Locale.getDefault();
  }

  @Override
  public boolean isRest() {
    return reqCtx.isRest();
  }

  @Override
  public String getToken() {
    return reqCtx.getToken();
  }

  @Override
  public String getRequestId() {
    return reqCtx.getRequestId();
  }

  @Override
  public Date getRequestStartTime() {
    return reqCtx.getRequestStartTime();
  }

  @Override
  public String getHostName() {
    return appCtx.getHostName();
  }

  @Override
  public String getApplicationId() {
    return appCtx.getApplicationId();
  }

  @Override
  public String getContextPath() {
    return appCtx.getContextPath();
  }

  @Override
  public void setRest(boolean rest) {
    reqCtx.setRest(rest);
  }

  @Override
  public void setToken(String token) {
    reqCtx.setToken(token);
  }

  @Override
  public Map<String, String> getTokenMap() {
    return appCtx.getTokenMap();
  }

}
