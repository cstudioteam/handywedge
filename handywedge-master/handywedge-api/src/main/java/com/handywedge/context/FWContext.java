/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;

import com.handywedge.user.FWUser;

/**
 * フレームワークコンテキスト情報のインターフェースです。<br>
 * 下記の情報を取得出来ます。<br>
 * <ul>
 * <li>ログイン中のユーザ情報</li>
 * <li>リクエストに関する情報</li>
 * <li>アプリケーションに関する情報</li>
 * </ul>
 * 但しREST APIの実装内ではセッションスコープにアクセス出来ないため、FWRESTContextから情報を取得して下さい。
 * 
 * @see FWRESTContext
 */
public interface FWContext {

  /**
   * リクエストIDを返します。
   *
   * @return リクエストID
   */
  String getRequestId();

  /**
   * リクエスト開始時刻を返します。
   *
   * @return リクエスト開始時刻
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
   * 前回リクエストの最終アクセス時間を返します。
   *
   * @return 前回リクエストの最終アクセス時間
   */
  Date getLastAccessTime();

  /**
   * アプリケーションのコンテキストパスを返します。
   *
   * @return コンテキストパス
   */
  String getContextPath();

  /**
   * ログイン中のユーザー情報を返します。
   * 
   * @return ログイン中のユーザー情報
   */
  FWUser getUser();

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
   * リクエストURLを返します。（クエリーストリングは無し）
   * 
   * @return リクエストURL
   */
  String getRequestUrl();
}
