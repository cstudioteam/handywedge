/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.mail;

import com.handywedge.common.FWException;

/**
 * メール送信機能で例外が発生した時にスローされる例外クラスです。
 */
public class FWMailException extends FWException {

  private static final long serialVersionUID = 1L;

  public FWMailException(String messageId, Throwable cause, Object... args) {

    super(messageId, cause, args);
  }

  public FWMailException(String messageId, Object... args) {

    super(messageId, args);
  }

}
