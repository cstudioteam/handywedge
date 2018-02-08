/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.mail;

/**
 * フレームワーク内部で使用するクラスです。<br>
 * アプリケーションでは使用しないで下さい。
 */
public interface FWMailSender {

  /**
   * メール送信処理。
   *
   * @param hostName SMTPホスト名
   * @param port SMTPポート番号
   * @param message メール情報
   * @throws FWMailSendException メール送信中にエラーが発生した場合
   */
  void send(String hostName, int port, FWMailMessage message) throws FWMailSendException;

}
