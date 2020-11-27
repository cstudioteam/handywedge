package com.handywedge.converter.topdf.exceptions;

import com.handywedge.common.FWException;

public class FWLockedWithPasswordException extends FWException {

  private static final long serialVersionUID = 1L;

  public FWLockedWithPasswordException(String code, Object... args) {
    super(code, args);
  }

  public FWLockedWithPasswordException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }
}
