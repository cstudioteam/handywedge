/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.handywedge.role.FWRoleAcl;
import com.handywedge.user.FWFullUser;
import com.handywedge.user.FWUser;

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

  @Override
  public List<FWRoleAcl> getRoleAcl() {
    return applicationContext.getRoleAcl();
  }

  @Override
  public String getRequestUrl() {
    return requestContext.getRequestUrl();
  }

  @Override
  public void setRequestUrl(String url) {
    requestContext.setRequestUrl(url);
  }

  @Override
  public boolean isUserManagementEnable() {
    return applicationContext.isUserManagementEnable();
  }

  @Override
  public void setUserManagementEnable(boolean userManagementEnable) {
    applicationContext.setUserManagementEnable(userManagementEnable);
  }

  @Override
  public String getPreToken() {
    return requestContext.getPreToken();
  }

  @Override
  public void setPreToken(String preToken) {
    requestContext.setPreToken(preToken);
  }
}
