package com.handywedge.converter.topdf.exceptions;

import com.handywedge.common.FWException;

public class FWUnsupportedFormatException extends FWException {
  private static final long serialVersionUID = 1L;

  public FWUnsupportedFormatException(String code) {
    super(code);
  }
}
