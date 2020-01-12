/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
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
public interface FWFullStatement extends FWStatement {

  void cancel() throws SQLException;

  SQLWarning getWarnings() throws SQLException;

  void clearWarnings() throws SQLException;

  void setCursorName(String name) throws SQLException;

  void setFetchDirection(int direction) throws SQLException;

  int getFetchDirection() throws SQLException;

  void setFetchSize(int rows) throws SQLException;

  int getFetchSize() throws SQLException;

  int getResultSetConcurrency() throws SQLException;

  int getResultSetType() throws SQLException;

  FWConnection getConnection() throws SQLException;

  int getResultSetHoldability() throws SQLException;

  void setPoolable(boolean poolable) throws SQLException;

  boolean isPoolable() throws SQLException;

  public void closeOnCompletion() throws SQLException;

  public boolean isCloseOnCompletion() throws SQLException;

  <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException;

  boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException;

}
