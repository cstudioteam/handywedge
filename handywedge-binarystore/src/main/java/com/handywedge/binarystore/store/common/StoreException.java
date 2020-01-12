/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

/**
 * 全てのストレージのチェック例外クラスのスーパークラスです。
 */
public class StoreException extends Exception {

  private static final long serialVersionUID = 1L;

  @Getter
  private ErrorClassification classification;

  @Getter
  @Setter
  private int httpStatus;

  @Setter
  private String message;

  private ResourceBundle rb = ResourceBundle.getBundle("Message_ja", Locale.JAPANESE);

  public StoreException(int httpStatus, ErrorClassification classification, Throwable cause,
      Object... args) {
    super(cause);
    this.httpStatus = httpStatus;
    this.classification = classification;
    this.message = rb.getString(classification.getErrorCode());
    if (args != null) {
      this.message = MessageFormat.format(this.message, args);
    }

  }

  public StoreException(int httpStatus, ErrorClassification classification, Object... args) {
    super();
    this.classification = classification;
    this.httpStatus = httpStatus;
    message = rb.getString(classification.getErrorCode());
    if (args != null) {
      message = MessageFormat.format(message, args);
    }
    message = "[" + classification.getErrorCode() + "] " + message;
  }

  @Override
  public String getMessage() {
    return message;
  }

}
