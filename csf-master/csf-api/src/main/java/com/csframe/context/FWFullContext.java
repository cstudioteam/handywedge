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

import com.csframe.user.FWFullUser;

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

}
