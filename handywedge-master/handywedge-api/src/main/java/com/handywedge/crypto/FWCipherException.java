/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.crypto;

import com.handywedge.common.FWRuntimeException;

/**
 * 暗号化・復号処理で例外が発生した時にスローされる例外クラスです。
 */
public class FWCipherException extends FWRuntimeException {

  private static final long serialVersionUID = 1L;

  public FWCipherException(String code, Object... args) {
    super(code, args);
  }

  public FWCipherException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }
}
