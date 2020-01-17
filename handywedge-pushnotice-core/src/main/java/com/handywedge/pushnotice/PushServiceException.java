package com.handywedge.pushnotice;

public class PushServiceException extends Exception {

  private static final long serialVersionUID = 1L;


  public PushServiceException() {

    super();
  }

  public PushServiceException(String message) {

    super(message);
  }

  public PushServiceException(Throwable t) {

    super(t);
  }

  public PushServiceException(String message, Throwable t) {

    super(message, t);
  }
}
