/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.mail;

/**
 * メール送信処理で例外が発生した場合にスローされます。
 */
public class FWMailSendException extends FWMailException {

  private static final long serialVersionUID = 1L;

  public FWMailSendException(String messageId, Object... args) {

    super(messageId, args);
  }

  public FWMailSendException(String messageId, Throwable cause, Object... args) {

    super(messageId, cause, args);
  }

}
