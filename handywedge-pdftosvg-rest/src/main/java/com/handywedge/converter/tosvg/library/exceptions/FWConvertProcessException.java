package com.handywedge.converter.tosvg.library.exceptions;

import java.text.MessageFormat;

public class FWConvertProcessException extends Exception {
  private static final long serialVersionUID = 1L;

  private String message;

  public FWConvertProcessException(String message, Object... args) {
    super();
    this.message = MessageFormat.format(message, args);
  }

  public FWConvertProcessException(String message, Throwable cause, Object... args) {
    super(cause);
    this.message = MessageFormat.format(message, args);
  }

  @Override
  public String getMessage() {
    return message;
  }
}
