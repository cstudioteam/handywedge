/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.io.Serializable;
import java.util.Date;

import com.csframe.user.FWFullUser;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。<br>
 * 
 * コンテキスト情報へのアクセスはFWContextインターフェースを使用して下さい。
 * 
 * @see FWContext
 */
public interface FWSessionContext extends Serializable {

  /**
   * 最終ログイン時間を返します。
   * 
   * @return 最終ログイン時間
   */
  Date getLastAccessTime();

  /**
   * 最終ログイン時間を設定します。
   * 
   * @param lastAccessTime 最終ログイン時間
   */
  void setLastAccessTime(Date lastAccessTime);

  /**
   * ログイン中のユーザー情報を返します。
   * 
   * @return ログイン中のユーザー情報
   */
  FWFullUser getUser();

  /**
   * ログイン中のユーザー情報を設定します。
   * 
   * @param user ログイン中のユーザー情報
   */
  void setUser(FWFullUser user);

}
