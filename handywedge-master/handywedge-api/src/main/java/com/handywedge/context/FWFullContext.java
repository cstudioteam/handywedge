/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.handywedge.role.FWRoleAcl;
import com.handywedge.user.FWFullUser;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。<br>
 * 
 * コンテキスト情報へのアクセスはFWContextインターフェースを使用して下さい。
 * 
 * @see FWContext
 */
public interface FWFullContext extends FWContext {

  /**
   * リクエストIDを設定します。
   *
   * @param requestId リクエストID
   */
  void setRequestId(String requestId);

  /**
   * リクエスト開始時刻を設定します。
   *
   * @param requestStartTime リクエスト開始時刻
   */
  void setRequestStartTime(Date requestStartTime);

  /**
   * 最終アクセス時間を設定します。
   *
   * @param lastAccessTime 最終アクセス時間
   */
  void setLastAccessTime(Date lastAccessTime);

  /**
   * ログイン中のユーザー情報を設定します。
   * 
   * @param user ユーザー情報
   */
  void setUser(FWFullUser user);

  /**
   * キャッシュされているAPIトークンを返します。
   * 
   * @return キャッシュされているトークンMap
   */
  Map<String, String> getTokenMap();

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

  /**
   * キャッシュされているロール別ACLの設定を取得します。
   * 
   * @return キャッシュされているACL設定
   */
  List<FWRoleAcl> getRoleAcl();

  /**
   * リクエストURLを設定します。
   * 
   * @param url リクエストURL
   */
  void setRequestUrl(String url);

  /**
   * fw_user_managementテーブルが存在する場合はtrueを返します。
   * 
   * @return ユーザー管理機能が有効の場合true
   * @since 0.4.0
   */
  boolean isUserManagementEnable();

  /**
   * fw_user_managementテーブルが存在するか設定します。
   * 
   * @param userManagementEnable fw_user_managementが存在する場合はtrue
   * @since 0.4.0
   */
  void setUserManagementEnable(boolean userManagementEnable);

  /**
   * リクエストで発行された仮登録トークンを返します。
   * 
   * @return 仮登録トークン
   * @since 0.4.0
   */
  String getPreToken();

  /**
   * リクエストで発行された仮登録トークンを設定します。
   * 
   * @param preToken 仮登録トークン
   * @since 0.4.0
   */
  void setPreToken(String preToken);

}
