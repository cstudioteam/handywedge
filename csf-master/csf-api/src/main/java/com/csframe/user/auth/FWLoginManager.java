/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user.auth;

/**
 * 認証に関連する処理のインターフェースです。
 */
public interface FWLoginManager {

  /**
   * 引数のユーザーIDとパスワードが正しいか認証します。<br>
   * 認証された場合はtrueを返し、同時にセッションのユーザー情報に値を設定します。<br>
   * 認証済のセッションでこのメソッドが呼ばれた場合は特に何もしません。
   * 
   * @param id ユーザーID
   * @param password パスワード
   * @return ユーザーIDとパスワードが正しい場合はtrue
   */
  boolean login(String id, String password);

  /**
   * セッションからユーザー情報を削除します。
   */
  void logout();

  /**
   * 引数のユーザーIDにAPIトークンを発行します。<br>
   * このメソッドでは認証処理は行われないので、必ずloginメソッドで認証を行ってから実行して下さい。<br>
   * 既にAPIトークンが発行されているユーザーの場合は再発行します。<br>
   * この場合、以前のAPIトークンは使用できなくなります。
   * 
   * @param id ユーザーID
   * @return 発行したAPIトークン
   */
  String publishAPIToken(String id);

  /**
   * 引数のユーザーIDに発行されているAPIトークンを返します。<br>
   * このメソッドでは認証処理は行われないので、必要であればloginメソッドで認証を行ってから実行して下さい。
   * 
   * @param id ユーザーID
   * @return APIトークン、発行されていない場合はnull
   */
  String getAPIToken(String id);

  /**
   * 引数のユーザーIDに発行されているAPIトークンを削除します。<br>
   * トークンが発行されていない場合は特に何もしません。
   * 
   * @param id ユーザーID
   */
  void removeAPIToken(String id);

  /**
   * APIトークンで認証を行います。<br>
   * 認証された場合はtrueを返し、同時にセッションのユーザー情報に値を設定します。<br>
   * 
   * @param token APIトークン
   * @return 認証された場合はtrue
   */
  boolean authAPIToken(String token);
}
