/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user.auth.rest;

import lombok.Data;

@Data
public class FWAPITokenRequest {

  private String id;
  private String password;

}
