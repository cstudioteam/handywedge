/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.role;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FWAction implements Serializable {

  private static final long serialVersionUID = 1L;
  private String actionCode;
  private String action;
  private String preStatus;
  private String postStatus;
}
