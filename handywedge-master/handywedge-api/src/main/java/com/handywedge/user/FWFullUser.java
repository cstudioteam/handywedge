/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user;

import java.sql.Timestamp;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。
 */
public interface FWFullUser extends FWUser {

  /**
   * ユーザーIDを設定します。
   *
   * @param id ユーザーID
   */
  void setId(String id);

  /**
   * ユーザー名を設定します。
   *
   * @param name ユーザー名
   */
  void setName(String name);

  /**
   * 最終ログイン時間を設定します。
   *
   * @param date 最終ログイン時間
   */
  void setLastLoginTime(Timestamp date);

  /**
   * 前回ログイン時間を設定します。
   *
   * @param beforeLoginTime 前回ログイン時間
   */
  void setBeforeLoginTime(Timestamp beforeLoginTime);

  /**
   * ロールコードを設定します。
   *
   * @param role ロールコード
   */
  void setRole(String role);

  /**
   * ロール名を設定します。
   *
   * @param roleName ロール名
   */
  void setRoleName(String roleName);
}
