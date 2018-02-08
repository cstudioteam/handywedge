/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

import java.io.Serializable;

import lombok.Data;

@Data
public class FWWFStatus implements Serializable {

  private static final long serialVersionUID = 1L;
  private String status;
  private String statusName;

}
