/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Locale;

/**
 * ユーザー情報インターフェースです。
 */
public interface FWUser extends Serializable {

  /**
   * ユーザーIDを返します。
   * 
   * @return ユーザーID
   */
  String getId();

  /**
   * ユーザー名を返します。
   * 
   * @return ユーザー名
   */
  String getName();

  /**
   * ユーザーのロケール情報を返します。
   * 
   * @return ロケール情報
   */
  Locale getLocale();

  /**
   * ユーザーのロケール情報を設定します。<br>
   * 通常はDBに登録されているロケール情報が設定されます。<br>
   * アプリケーションで上書きしたい場合はメソッドを実行して下さい。<br>
   * 
   * @param locale ロケール情報
   */
  void setLocale(Locale locale);

  /**
   * 最終ログイン時間を返します。
   * 
   * @return 最終ログイン時間
   */
  Timestamp getLastLoginTime();

  /**
   * 前回ログイン時間を返します。
   * 
   * @return 前回ログイン時間
   */
  Timestamp getBeforeLoginTime();

  /**
   * ユーザーに設定されているロールを返します。<br>
   * ロールの設定がない場合はnullとなります。
   * 
   * @return ロール
   */
  String getRole();

}
