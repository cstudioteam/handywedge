/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

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
   * 認証がAPIトークンによる場合はtrueを返します。
   * 
   * @return リクエストがAPIトークン認証の場合はtrue
   */
  boolean isAPITokenAuth();

  /**
   * リクエストがAPIトークン認証されている場合はtrueを設定します。
   * 
   * @param apiToken トークン認証の場合はtrue
   */
  void setAPITokenAuth(boolean apiToken);

}
