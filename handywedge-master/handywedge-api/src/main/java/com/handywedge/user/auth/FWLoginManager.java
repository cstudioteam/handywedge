/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user.auth;

/**
 * 認証に関連する処理のインターフェースです。
 */
public interface FWLoginManager {

  /**
   * 引数のユーザーIDとパスワードが正しいか認証します。<br>
   * 認証された場合はtrueを返し、同時にセッションのユーザー情報に値を設定します。<br>
   * 認証済のセッションでこのメソッドが呼ばれた場合は特に何もしません。<br>
   * 仮登録ユーザーの場合はパスワードが正しい場合でもfalseとなります。
   *
   * @param id ユーザーID
   * @param password パスワード
   * @return ユーザーIDとパスワードが正しい場合はtrue
   */
  boolean login(String id, String password);

  /**
   * 認証なしでログイン処理のみ行います。<br>
   * このメソッドを実行する場合は事前に認証処理を実施して下さい。<br>
   *
   * @param id ユーザーID
   */
  void login(String id);

  /**
   * ログイン操作はせず、ユーザーIDとパスワードが正しいか確認のみ行います。<br>
   * 仮登録ユーザーの場合はパスワードが正しい場合でもfalseとなります。<br>
   *
   * @param id ユーザーID
   * @param password パスワード
   * @return ユーザーIDとパスワードが正しい場合はtrue
   */
  boolean checkPassword(String id, String password);

  /**
   * セッションからユーザー情報を削除します。
   */
  void logout();

  /**
   * 引数のユーザーIDにAPIトークンを発行します。<br>
   * このメソッドでは認証処理は行われないので、必ずloginメソッドで認証を行ってから実行して下さい。<br>
   * multiple=falseの場合、既にAPIトークンが発行されているユーザーは再発行します。<br>
   * この場合、以前のAPIトークンは使用できなくなります。
   *
   * @param id ユーザーID
   * @param multiple 多重発行する場合はtrue
   * @return 発行したAPIトークン
   */
  String publishAPIToken(String id, boolean multiple);

  /**
   * 引数のトークンを削除します。<br>
   * トークンが発行されていない場合は特に何もしません。
   *
   * @param token トークン
   */
  void removeAPIToken(String token);

  /**
   * APIトークンで認証を行います。<br>
   * 認証された場合はtrueを返します。<br>
   *
   * @param token APIトークン
   * @return 認証された場合はtrue
   */
  boolean authAPIToken(String token);

  /**
   * APIトークンの有効期限をチェックします。<br>
   * 有効期限内の場合はtrueを返します。<br>
   *
   * @param token APIトークン
   * @return 有効期限内の場合はtrue
   *
   * @since 0.6.0
   */
  boolean expirationAPIToken(String token);
}
