/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.common;

public class FWSessionTimeoutException extends FWRuntimeException {

  private static final long serialVersionUID = 1L;

  public FWSessionTimeoutException(String code, Object... args) {
    super(code, args);
  }
}
