package com.handywedge.converter.tosvg.exceptions;

import com.handywedge.common.FWException;

public class FWConvertProcessException extends FWException {
  private static final long serialVersionUID = 1L;

  public FWConvertProcessException(String code, Object... args) {
    super(code, args);
  }

  public FWConvertProcessException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }
}
