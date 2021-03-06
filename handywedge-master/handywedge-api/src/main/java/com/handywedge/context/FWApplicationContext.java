/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.List;

import com.handywedge.role.FWRoleAcl;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。<br>
 *
 * コンテキスト情報へのアクセスはFWContextインターフェースを使用して下さい。
 *
 * @see FWContext
 */
public interface FWApplicationContext {

  String getHostName();

  void setHostName(String hostName);

  String getApplicationId();

  void setApplicationId(String applicationId);

  String getContextPath();

  void setContextPath(String contextPath);

  List<FWRoleAcl> getRoleAcl();

  boolean isUserManagementEnable();

  void setUserManagementEnable(boolean userManagementEnable);
}
