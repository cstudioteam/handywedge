/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;

public class FWInternalConnectionManager {

  public static Connection getConnection() {
    return getConnection("jdbc/fw");
  }

  public static Connection getConnection(String dsName) {
    DataSource dataSource;
    try {
      Thread l_thread = Thread.currentThread();
      l_thread.setContextClassLoader(FWInternalConnectionManager.class.getClassLoader());
      dataSource = (DataSource) InitialContext.doLookup("java:comp/env/" + dsName);
    } catch (NamingException e) {
      throw new FWRuntimeException(FWConstantCode.DS_LOOKUP_FAIL, e);
    }
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }
}
