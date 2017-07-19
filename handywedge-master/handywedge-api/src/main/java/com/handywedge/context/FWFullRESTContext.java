/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Locale;
import java.util.Map;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。<br>
 *
 */
public interface FWFullRESTContext extends FWRESTContext {


  /**
   * ユーザーIDを設定します。
   *
   * @param userId ユーザーID
   */
  void setUserId(String userId);

  /**
   * ユーザー名を設定します。
   *
   * @param userName ユーザー名
   */
  void setUserName(String userName);


  /**
   * ロールコードを設定します。
   *
   * @param userRole ロールコード
   */
  void setUserRole(String userRole);

  /**
   * ロール名を設定します。
   *
   * @param userRoleName ロール名
   */
  void setUserRoleName(String userRoleName);

  /**
   * ユーザーのロケール情報を設定します。
   *
   * @param userLocale ロケール情報
   */
  void setUserLocale(Locale userLocale);

  /**
   * リクエストがREST APIであるか設定します。
   *
   * @param rest リクエストがREST APIである場合はtrue
   */
  void setRest(boolean rest);

  /**
   * 認証されたAPIトークンを設定します。
   *
   * @param token APIトークン
   */
  void setToken(String token);

  Map<String, String> getTokenMap();

}
