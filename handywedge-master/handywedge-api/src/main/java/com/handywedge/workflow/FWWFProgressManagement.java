/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
class FWWFProgressManagement implements Serializable {

  private static final long serialVersionUID = 1L;
  private String wfId;
  private int wfSerNo;
  private String actionCode;
  private String statusCode;
  private Timestamp createDate;

}
