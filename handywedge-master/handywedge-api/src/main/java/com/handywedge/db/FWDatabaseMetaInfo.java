/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.db;

import java.util.ResourceBundle;

/**
 * handywedgeスキーマで扱うテーブルのメタ情報を取得するためのクラスです。
 */
public class FWDatabaseMetaInfo {

  private static ResourceBundle rb = ResourceBundle.getBundle("com.handywedge.db.fw_db_metadata");

  public static int getUserIdLength() {
    return Integer.parseInt(rb.getString("user.id"));
  }

  public static int getUserNameLength() {
    return Integer.parseInt(rb.getString("user.name"));
  }

  public static int getUserRoleLength() {
    return Integer.parseInt(rb.getString("user.role"));
  }

  public static int getUserCountry() {
    return Integer.parseInt(rb.getString("user.country"));
  }

  public static int getUserLanguage() {
    return Integer.parseInt(rb.getString("user.language"));
  }
}
