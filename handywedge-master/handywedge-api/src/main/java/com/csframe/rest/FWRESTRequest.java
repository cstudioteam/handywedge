/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest;

/**
 * クライアントから送信されたJSONデータをアンマーシャルするJSONクラスです。
 */
public abstract class FWRESTRequest {

  /**
   * ロギングのためリクエスト内容を文字列で表現するtoStringを実装します。
   */
  @Override
  public abstract String toString();

}
