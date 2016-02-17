package com.csframe.user.auth;

import com.csframe.common.FWException;

public class FWAuthException extends FWException {

  private static final long serialVersionUID = 1L;

  public FWAuthException(String code, Throwable cause) {
    super(code, cause);
  }

}
