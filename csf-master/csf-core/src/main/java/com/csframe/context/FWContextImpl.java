/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.util.Date;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.csframe.user.FWFullUser;
import com.csframe.user.FWUser;

@RequestScoped
@Named("fwContext")
public class FWContextImpl implements FWFullContext {

  @Inject
  private FWApplicationContext applicationContext;

  @Inject
  private FWSessionContext sessionContext;

  @Inject
  private FWRequestContext requestContext;

  @Override
  public String getRequestId() {
    return requestContext.getRequestId();
  }

  @Override
  public void setRequestId(String requestId) {
    requestContext.setRequestId(requestId);
  }

  @Override
  public String getApplicationId() {
    return applicationContext.getApplicationId();
  }

  @Override
  public Date getLastAccessTime() {
    return sessionContext.getLastAccessTime();
  }

  @Override
  public void setLastAccessTime(Date lastAccessTime) {
    sessionContext.setLastAccessTime(lastAccessTime);
  }

  @Override
  public String getContextPath() {
    return applicationContext.getContextPath();
  }

  @Override
  public String getHostName() {
    return applicationContext.getHostName();
  }

  @Override
  public Date getRequestStartTime() {
    return requestContext.getRequestStartTime();
  }

  @Override
  public void setRequestStartTime(Date requestStartTime) {
    requestContext.setRequestStartTime(requestStartTime);
  }

  @Override
  public void setUser(FWFullUser user) {
    sessionContext.setUser(user);
  }

  @Override
  public FWUser getUser() {
    return sessionContext.getUser();
  }

  @Override
  public Map<String, String> getTokenMap() {
    return applicationContext.getTokenMap();
  }

  @Override
  public boolean isRest() {
    return requestContext.isRest();
  }

  @Override
  public String getToken() {
    return requestContext.getToken();
  }

  @Override
  public void setRest(boolean rest) {
    requestContext.setRest(rest);
  }

  @Override
  public void setToken(String token) {
    requestContext.setToken(token);
  }
}
