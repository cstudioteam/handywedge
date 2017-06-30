/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

/**
 * ログ出力種類を定義します。<br>
 * アプリケーションで使用することはありません。
 */
public enum FWLogName {

  /**
   * フレームワークログを示します。
   */
  FW,
  /**
   * アプリケーションログを示します。
   */
  AP,
  /**
   * 処理時間計測ログを示します。
   */
  PERF,
  /**
   * リクエスト処理時間計測ログを示します。
   */
  RESP,
  /**
   * JDBC関連のログを示します。
   */
  JDBC;
}
