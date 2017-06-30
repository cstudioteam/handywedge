/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.role;

public class FWAccessForbidden extends FWRoleException {

  private static final long serialVersionUID = 1L;

  public FWAccessForbidden(String code, Object... args) {
    super(code, args);
  }
}
