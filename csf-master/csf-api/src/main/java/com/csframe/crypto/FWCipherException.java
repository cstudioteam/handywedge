package com.csframe.crypto;

import com.csframe.common.FWRuntimeException;

public class FWCipherException extends FWRuntimeException {

  private static final long serialVersionUID = 1L;

  public FWCipherException(String code, Object... args) {
    super(code, args);
  }

  public FWCipherException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }
}
