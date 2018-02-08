/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.db;

import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。
 */
public interface FWFullResultSet extends FWResultSet {

  SQLWarning getWarnings() throws SQLException;

  void clearWarnings() throws SQLException;

  String getCursorName() throws SQLException;

}
