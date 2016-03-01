package com.csframe.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.context.FWApplicationContext;
import com.csframe.db.FWInternalConnectionManager;
import com.csframe.log.FWLogger;

@ApplicationScoped
public class FWInternalUtil {

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWLogger logger;

  // @FWTransactional(dataSourceName = "jdbc/fw")
  public void cacheAPIToken() {
    long start = logger.perfStart("cacheAPIToken");
    Statement st = null;
    ResultSet rs = null;
    Connection con = null;
    Map<String, String> tokens = appCtx.getTokenMap();
    try {
      // Requestスコープインスタンスが注入されていない段階なので例外的に生のConnectionを操作する。
      con = FWInternalConnectionManager.getConnection();
      st = con.createStatement();
      rs = st.executeQuery("select id, token from fw_api_token");
      while (rs.next()) {
        tokens.put(rs.getString("id"), rs.getString("token"));
      }
      logger.info("初期キャッシュAPIトークン数={}", tokens.size());
      logger.perfEnd("cacheAPIToken", start);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (Exception e) {
      }
      try {
        if (st != null) {
          st.close();
        }
      } catch (Exception e) {
      }
      try {
        if (con != null) {
          con.close();
        }
      } catch (Exception e) {
      }
    }
  }
}
