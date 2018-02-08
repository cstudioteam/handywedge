/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.db;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。
 */
public interface FWFullConnectionManager extends FWConnectionManager {

  FWFullConnection getConnection(String dataSource);

  @Override
  FWFullConnection getConnection();

  void addStatement(FWStatement statement);

  void addResltSet(FWResultSet resultSet);

  void close();
}
