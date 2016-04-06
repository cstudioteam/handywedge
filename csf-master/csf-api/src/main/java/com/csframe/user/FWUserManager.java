/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user;

import com.csframe.user.auth.FWLoginManager;

/**
 * ユーザーマスターを管理するためのインターフェースです。
 */
public interface FWUserManager {

  /**
   * ユーザーIDを登録します。パスワードはプレーン文字列を渡し、インターフェース実装でハッシュ化されます。
   * 
   * @param id ユーザーID
   * @param password パスワード（プレーン）
   * @return 追加できた場合はtrue
   */
  boolean register(String id, String password);

  /**
   * パスワードの変更をします。<br>
   * パスワードチェックは行わないので必要であればこのメソッドを実行前にパスワードチェックを行って下さい。
   * 
   * @param id ユーザーID
   * @param password 変更後パスワード（プレーン）
   * @return 更新出来た場合はtrue
   * @see FWLoginManager
   */
  boolean changePassword(String id, String password);

  /**
   * パスワード以外のユーザー情報を更新します。
   * 
   * @param user ユーザー情報
   */
  void update(FWUserData user);

  /**
   * ユーザー情報を物理削除します。削除したデータはフレームワークでは復元できません。
   * 
   * @param id ユーザーID
   */
  void delete(String id);
}
