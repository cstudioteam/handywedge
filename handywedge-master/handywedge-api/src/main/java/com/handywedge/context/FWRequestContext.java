/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。<br>
 * 
 * コンテキスト情報へのアクセスはFWContextインターフェースを使用して下さい。
 * 
 * @see FWContext
 */
public interface FWRequestContext {

  /**
   * リクエストIDを返します。
   * 
   * @return リクエストID
   */
  String getRequestId();

  /**
   * リクエストIDを設定します。
   * 
   * @param requestId リクエストID
   */
  void setRequestId(String requestId);

  /**
   * リクエスト開始時間を返します。
   * 
   * @return リクエスト開始時間
   */
  Date getRequestStartTime();

  /**
   * リクエスト開始時間を設定します。
   * 
   * @param requestStartTime リクエスト開始時間
   */
  void setRequestStartTime(Date requestStartTime);

  /**
   * リクエストがREST APIの場合はtrueを返します。
   * 
   * @return リクエストがREST APIの場合はtrue
   */
  boolean isRest();

  /**
   * リクエストで認証されたAPIトークンを返します。
   * 
   * @return APIトークン
   */
  String getToken();

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
   * リクエストURLを返します。（クエリーストリングは無し）
   * 
   * @return リクエストURL
   */
  String getRequestUrl();

  /**
   * リクエストURLを設定します。
   * 
   * @param url リクエストURL
   */
  void setRequestUrl(String url);

  /**
   * リクエストで発行された仮登録トークンを返します。
   * 
   * @return 仮登録トークン
   */
  String getPreToken();

  /**
   * リクエストで発行された仮登録トークンを設定します。
   * 
   * @param preToken 仮登録トークン
   */
  void setPreToken(String preToken);

}
