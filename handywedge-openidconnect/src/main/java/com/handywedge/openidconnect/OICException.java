/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect;

/**
 * OpenID Connect例外クラス。
 *
 * @author takeuchi
 */
public class OICException extends Exception {

  private static final long serialVersionUID = 1L;

  public OICException() {
    super();
  }

  public OICException(String msg) {
    super(msg);
  }

  public OICException(String msg, Throwable t) {
    super(msg, t);
  }

  public OICException(Throwable t) {
    super(t);
  }
}
