/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.context.FWApplicationContext;
import com.csframe.db.FWInternalConnectionManager;
import com.csframe.log.FWLogger;
import com.csframe.role.FWRoleAcl;

@ApplicationScoped
public class FWInternalUtil {

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWLogger logger;

  // @FWTransactional(dataSourceName = "jdbc/fw")
  public void cacheAPIToken() {
    long start = logger.perfStart("cacheAPIToken");
    Map<String, String> tokens = appCtx.getTokenMap();

    // Requestスコープインスタンスが注入されていない段階なので例外的に生のConnectionを操作する。
    try (Connection con = FWInternalConnectionManager.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT id, token FROM fw_api_token");) {
      while (rs.next()) {
        tokens.put(rs.getString("id"), rs.getString("token"));
      }
      logger.info("初期キャッシュAPIトークン数={}", tokens.size());
      logger.perfEnd("cacheAPIToken", start);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  public void cacheRoleAcl() {
    long start = logger.perfStart("cacheRoleAcl");
    List<FWRoleAcl> acl = appCtx.getRoleAcl();

    // Requestスコープインスタンスが注入されていない段階なので例外的に生のConnectionを操作する。
    try (Connection con = FWInternalConnectionManager.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT role, url_pattern FROM fw_role_acl");) {
      while (rs.next()) {
        acl.add(new FWRoleAcl(rs.getString("role"), rs.getString("url_pattern")));
      }
      logger.info("role acl設定数={}", acl.size());
      logger.perfEnd("cacheRoleAcl", start);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }
}
