/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.log.FWLogger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class FWConnectionManagerImpl implements FWFullConnectionManager {

  // REQUIRED以外のネストしたトランザクションは考慮していない
  private FWFullConnection connection = null;
  private List<FWStatement> statements = new ArrayList<>();
  private List<FWResultSet> resultSets = new ArrayList<>();

  @Inject
  private FWLogger logger;

  @Override
  public FWFullConnection getConnection(String dataSourceName) {

    DataSource dataSource;
    try {
      dataSource = (DataSource) InitialContext.doLookup("java:comp/env/" + dataSourceName);
    } catch (NamingException e) {
      throw new FWRuntimeException(FWConstantCode.DS_LOOKUP_FAIL, e);
    }
    try {
      connection = new FWConnectionWrapper(dataSource.getConnection());
      return connection;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public FWFullConnection getConnection() {

    return connection;
  }

  @Override
  public void addStatement(FWStatement statement) {

    statements.add(statement);
  }

  @Override
  public void addResltSet(FWResultSet resultSet) {

    resultSets.add(resultSet);
  }

  @Override
  public void close() {

    try {
      for (FWResultSet rs : resultSets) {
        if (!rs.isClosed()) {
          logger.warn("FWResulstSet not closed.");
          try {
            rs.close();
          } catch (SQLException e) {
            logger.warn("FWResultSet close error.", e);
          }
        }
      }
      for (FWStatement s : statements) {
        if (!s.isClosed()) {
          logger.warn("FWStatement not closed.");
          try {
            s.close();
          } catch (SQLException e) {
            logger.warn("FWStatement close error.", e);
          }
        }
      }
      if (connection != null) {
        connection.close();
        connection = null;
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

}
