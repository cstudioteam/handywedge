/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.util.Date;
import java.util.Locale;

/**
 * REST API内の実装で使用するコンテキストです。<br>
 * REST APIではセッションコンテキストにアクセスできないため、トークンで認証されたユーザー情報などはこのインターフェースを通じて取得します。<br>
 *
 */
public interface FWRESTContext {

  /**
   * ユーザーIDを返します。
   * 
   * @return ユーザーID
   */
  String getUserId();

  /**
   * ユーザー名を返します。
   * 
   * @return ユーザー名
   */
  String getUserName();

  /**
   * ユーザーのロケール情報を返します。
   * 
   * @return ロケール情報
   */
  Locale getUserLocale();

  /**
   * ユーザーに設定されているロールを返します。<br>
   * ロールの設定がない場合はnullとなります。
   * 
   * @return ロール
   */
  String getUserRole();

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
   * リクエストIDを返します。
   * 
   * @return リクエストID
   */
  String getRequestId();

  /**
   * リクエスト開始時間を返します。
   * 
   * @return リクエスト開始時間
   */
  Date getRequestStartTime();

  /**
   * 実行中のサーバーのホスト名を返します。
   *
   * @return 実行中のサーバーの名称
   */
  String getHostName();

  /**
   * アプリケーションIDを返します。
   *
   * @return アプリケーションID
   */
  String getApplicationId();

  /**
   * アプリケーションのコンテキストパスを返します。
   *
   * @return コンテキストパス
   */
  String getContextPath();
}
