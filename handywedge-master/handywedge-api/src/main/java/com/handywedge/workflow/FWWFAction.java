/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

import java.io.Serializable;

import lombok.Data;

@Data
public class FWWFAction implements Serializable {

  private static final long serialVersionUID = 1L;
  private String wfId;
  private String actionCode;
  private String action;
  private String preStatus;
  private String preStatusName;
  private String postStatus;
  private String postStatusName;
  private boolean rollbackAction;
  private boolean lastAction;
}
