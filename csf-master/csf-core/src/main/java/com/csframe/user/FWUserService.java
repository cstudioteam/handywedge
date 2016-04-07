/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.csframe.common.FWPasswordUtil;
import com.csframe.db.FWConnection;
import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWTransactional;
import com.csframe.log.FWLogger;

@ApplicationScoped
public class FWUserService {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWLogger logger;

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int insert(String id, String passwd) throws SQLException {
    long startTime = logger.perfStart("insert");

    int result = 0;
    FWConnection con = cm.getConnection();
    // ID登録
    try (FWPreparedStatement selectPs =
        con.prepareStatement("SELECT id FROM fw_user WHERE id = ?");
        FWPreparedStatement insertPs =
            con.prepareStatement("INSERT INTO fw_user (id) VALUES(?)");) {
      selectPs.setString(1, id);
      try (FWResultSet rs = selectPs.executeQuery()) {
        if (rs.next()) {
          logger.warn("user_id exists. id={}", id);
          return 0;
        }
      }

      // パフォーマンスを考慮して排他処理とかは行わない（刹那の一意制約違反はスロー）
      insertPs.setString(1, id);
      result = insertPs.executeUpdate();
      logger.debug("id insert result={}", result);
    }

    // パスワード登録
    try (FWPreparedStatement ps =
        con.prepareStatement("INSERT INTO fw_user_passwd (id, passwd) VALUES(?, ?)")) {
      ps.setString(1, id);
      ps.setString(2, FWPasswordUtil.createPasswordHash(passwd));
      int passwdResult = ps.executeUpdate();
      logger.debug("passwd insert result={}", passwdResult);
    }
    logger.perfEnd("insert", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int changePassword(String id, String passwd) throws SQLException {
    long startTime = logger.perfStart("changePassword");
    FWConnection con = cm.getConnection();
    int passwdResult = 0;
    try (FWPreparedStatement selectPs =
        con.prepareStatement("SELECT * FROM fw_user_passwd WHERE id = ? FOR UPDATE");
        FWPreparedStatement updatePs =
            con.prepareStatement(
                "UPDATE fw_user_passwd SET passwd = ?, update_date = ? where id = ?")) {
      selectPs.setString(1, id);
      try (FWResultSet rs = selectPs.executeQuery()) {
        if (rs.next()) {
          updatePs.setString(1, FWPasswordUtil.createPasswordHash(passwd));
          // SQL関数で更新するとDBMSによって関数が異なりそうなのでJDBCから更新
          updatePs.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
          updatePs.setString(3, id);
          passwdResult = updatePs.executeUpdate();
          logger.debug("passwd update result={}", passwdResult);
        } else {
          logger.warn("user_id not exists. id={}", id);
        }
      }
    }
    logger.perfEnd("changePassword", startTime);
    return passwdResult;
  }
}
