/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.role;

import com.csframe.common.FWException;

public class FWRoleException extends FWException {

  private static final long serialVersionUID = 1L;

  public FWRoleException(String code, Object... args) {
    super(code, args);
  }
}
