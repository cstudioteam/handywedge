/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
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
public class FWWFLog implements Serializable {

  private static final long serialVersionUID = 1L;
  private String wfId;
  private int wfSerNo;
  private Timestamp actionDate;
  private String actionOwner;
  private String actionOwnerName;
  private String actionCode;
  private String actionName;
  private String statusCode;
  private String statusName;
  private String description;

}
