/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.common;

/**
 * 全てのhandywedgeのチェック例外クラスのスーパークラスです。
 */
public class FWException extends Exception {

  private static final long serialVersionUID = 1L;

  private String code;
  private Object[] args;

  public FWException(String code, Throwable cause, Object... args) {
    super(cause);
    this.code = code;
    this.args = args;
  }

  public FWException(String code, Object... args) {
    super();
    this.code = code;
    this.args = args;
  }

  @Override
  public String getMessage() {
    return FWErrorMessage.getMessage(code, args);
  }

  public String getErrorCode() {
    return code;
  }

}
