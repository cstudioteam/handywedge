/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.notice;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.ToString;

/**
 * お知らせ情報を保持するクラスです。
 */
@Data
@ToString
public class FWNotice implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;
  private String notice;
  private Timestamp createDate;
  private Timestamp updateDate;

}
