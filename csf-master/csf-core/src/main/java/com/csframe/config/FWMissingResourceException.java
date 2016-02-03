package com.csframe.config;

import com.csframe.common.FWRuntimeException;

public class FWMissingResourceException extends FWRuntimeException {

  private static final long serialVersionUID = 1L;

  public FWMissingResourceException(String code, Object... args) {
    super(code, args);
  }

  public FWMissingResourceException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }
}
