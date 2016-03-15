/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.util.Date;

import com.csframe.user.FWUser;

/**
 * フレームワークコンテキスト情報のインターフェースです。<br>
 * 下記の情報を取得出来ます。<br>
 * <ul>
 * <li>ログイン中のユーザ情報</li>
 * <li>リクエストに関する情報</li>
 * <li>アプリケーションに関する情報</li>
 * </ul>
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
   * 認証がAPIトークンによる場合はtrueを返します。
   * 
   * @return リクエストがAPIトークン認証の場合はtrue
   */
  boolean isAPITokenAuth();

  /**
   * ログイン中のユーザー情報を返します。
   * 
   * @return ログイン中のユーザー情報
   */
  FWUser getUser();
}
