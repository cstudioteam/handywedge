/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * クライアントに応答するためのJSONをマーシャルするためのクラスです。<br>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class FWRESTResponse {

  private int return_cd;
  private String return_msg;

  /**
   * リターンコードを返します。
   * 
   * @return リターンコード
   */
  public int getReturn_cd() {
    return return_cd;
  }

  /**
   * リターンコードを設定します。<br>
   * エラー応答を示す場合は負の値を設定して下さい。<br>
   * 但し、-9000以下はフレームワークのエラーコードで使用するため使用しないで下さい。
   * 
   * @param return_cd リターンコード
   */
  public void setReturn_cd(int return_cd) {
    this.return_cd = return_cd;
  }

  /**
   * リターンメッセージを返します。
   * 
   * @return リターンメッセージ
   */
  public String getReturn_msg() {
    return return_msg;
  }

  /**
   * リターンメッセージを設定します。
   * 
   * @param return_msg リターンメッセージ
   */
  public void setReturn_msg(String return_msg) {
    this.return_msg = return_msg;
  }

  /**
   * ロギングのためレスポンス内容を文字列で表現するtoStringを実装します。
   */
  @Override
  public abstract String toString();

}
