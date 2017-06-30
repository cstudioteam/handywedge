/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.common.FWPasswordUtil;
import com.handywedge.common.FWStringUtil;
import com.handywedge.config.FWMessageResources;
import com.handywedge.context.FWFullContext;
import com.handywedge.db.FWConnection;
import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;
import com.handywedge.log.FWLogger;
import com.handywedge.user.FWUserData;
import com.handywedge.user.auth.FWLoginManager;
import com.handywedge.util.FWInternalUtil;

@ApplicationScoped
public class FWUserService {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWLogger logger;

  @Inject
  private FWFullContext ctx;

  @Inject
  private FWMessageResources fwMsg;

  @Inject
  private FWLoginManager loginMgr;

  private final String PRE_USER_REGISTER_TOKEN_EXPIRE = "fw.pre.user.register.token.expire";
  private final String USER_RESET_TOKEN_EXPIRE = "fw.user.register.reset.passwd.token.expire";
  private final String DEFAULT_EXPIRE = "4320"; // デフォルト72時間

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int insert(String id, String passwd) throws SQLException {
    return insert(id, passwd, null, null);
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int insert(String id, String passwd, Integer preRegister, String mailAddress)
      throws SQLException {
    long startTime = logger.perfStart("insert");

    int result = 0;
    FWConnection con = cm.getConnection();

    // ユーザー管理が有効な場合、まずは管理テーブルをチェックして必要に応じでdelete
    if (ctx.isUserManagementEnable()) {
      String sql = "SELECT * FROM fw_user_management WHERE id = ?";
      try (FWPreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, id);
        try (FWResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            boolean pre = rs.getBoolean("pre_register");
            if (!pre) {
              logger.warn("user_id exists. id={}", id);
              return 0;
            } else { // 仮登録状態でregisterが呼ばれた場合は削除してから処理する
              logger.debug("仮登録データを削除します。id={}", id);
              String deleteSql = "DELETE FROM fw_user_management WHERE id = ?";
              String deleteSql2 = "DELETE FROM fw_user_passwd WHERE id = ?";
              String deleteSql3 = "DELETE FROM fw_user WHERE id = ?";
              try (FWPreparedStatement delPs = con.prepareStatement(deleteSql)) {
                delPs.setString(1, id);
                delPs.executeUpdate();
              }
              try (FWPreparedStatement delPs = con.prepareStatement(deleteSql2)) {
                delPs.setString(1, id);
                delPs.executeUpdate();
              }
              try (FWPreparedStatement delPs = con.prepareStatement(deleteSql3)) {
                delPs.setString(1, id);
                delPs.executeUpdate();
              }
            }
          }
        }
      }
    }

    // ID登録
    String insertSql;
    if (!FWStringUtil.isEmpty(mailAddress)) {
      insertSql = "INSERT INTO fw_user (id, mail_address) VALUES(?, ?)";
    } else {
      insertSql = "INSERT INTO fw_user (id) VALUES(?)";
    }
    try (FWPreparedStatement selectPs =
        con.prepareStatement("SELECT id FROM fw_user WHERE id = ?");
        FWPreparedStatement insertPs =
            con.prepareStatement(insertSql);) {
      selectPs.setString(1, id);
      try (FWResultSet rs = selectPs.executeQuery()) {
        if (rs.next()) {
          logger.warn("user_id exists. id={}", id);
          return 0;
        }
      }

      // パフォーマンスを考慮して排他処理とかは行わない（刹那の一意制約違反はスロー）
      insertPs.setString(1, id);
      if (!FWStringUtil.isEmpty(mailAddress)) {
        insertPs.setString(2, mailAddress);
      }
      result = insertPs.executeUpdate();
      logger.debug("id insert. result={}", result);
    }

    // パスワード登録
    try (FWPreparedStatement ps =
        con.prepareStatement("INSERT INTO fw_user_passwd (id, passwd) VALUES(?, ?)")) {
      ps.setString(1, id);
      ps.setString(2, FWPasswordUtil.createPasswordHash(passwd));
      int passwdResult = ps.executeUpdate();
      logger.debug("passwd insert. result={}", passwdResult);
    }

    // 仮登録オプションが有効な場合
    if (preRegister != null && preRegister > 0) {
      String sql = "INSERT INTO fw_user_management (id, pre_token) VALUES(?, ?)";
      String token = FWInternalUtil.generateToken();
      try (FWPreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, id);
        ps.setString(2, token);
        ps.executeUpdate();
      }
      ctx.setPreToken(token);
      logger.debug("preToken insert. token={}", token);
    }

    logger.perfEnd("insert", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int update(FWUserData user) throws SQLException {
    long startTime = logger.perfStart("update");

    int result = 0;
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps =
        con.prepareStatement(
            "UPDATE fw_user SET name = ?, role = ?, country = ?, language = ?, update_date = ? WHERE id = ?")) {
      int idx = 0;
      ps.setString(++idx, user.getName());
      ps.setString(++idx, user.getRole());
      ps.setString(++idx, user.getLocale().getCountry());
      ps.setString(++idx, user.getLocale().getLanguage());
      // TODO DB時間の採用とかも検討したい。Localeも検討したい。
      ps.setTimestamp(++idx, new Timestamp(System.currentTimeMillis()));
      ps.setString(++idx, user.getId());
      result = ps.executeUpdate();
      logger.debug("update. result={}", result);
    }
    logger.perfEnd("update", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int delete(String id) throws SQLException {
    long startTime = logger.perfStart("delete");

    int result = 0;
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement("DELETE FROM fw_api_token  WHERE id = ?")) {
      ps.setString(1, id);
      result = ps.executeUpdate();
      logger.debug("delete fw_api_token. result={}", result);
    }
    try (
        FWPreparedStatement ps = con.prepareStatement("DELETE FROM fw_user_passwd  WHERE id = ?")) {
      ps.setString(1, id);
      result = ps.executeUpdate();
      logger.debug("delete fw_user_passwd. result={}", result);
    }

    if (ctx.isUserManagementEnable()) {
      try (
          FWPreparedStatement ps =
              con.prepareStatement("DELETE FROM fw_user_management  WHERE id = ?")) {
        ps.setString(1, id);
        result = ps.executeUpdate();
        logger.debug("delete fw_user_management. result={}", result);
      }
    }

    try (FWPreparedStatement ps = con.prepareStatement("DELETE FROM fw_user WHERE id = ?")) {
      ps.setString(1, id);
      result = ps.executeUpdate();
      logger.debug("delete fw_user. result={}", result);
    }
    logger.perfEnd("delete", startTime);
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
          logger.debug("passwd update. result={}", passwdResult);
        } else {
          logger.warn("user_id not exists. id={}", id);
        }
      }
    }
    logger.perfEnd("changePassword", startTime);
    return passwdResult;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWUserManagerPreRegisterStatus validPreToken(String preToken) throws SQLException {
    long startTime = logger.perfStart("validPreToken");
    FWUserManagerPreRegisterStatus result;
    String sql = "SELECT * FROM fw_user_management WHERE pre_token = ?";
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, preToken);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          boolean pre = rs.getBoolean("pre_register");
          if (!pre) {
            result = FWUserManagerPreRegisterStatus.REGISTER;
          } else {
            int expire =
                Integer.parseInt(
                    FWStringUtil.replaceNullString(fwMsg.get(PRE_USER_REGISTER_TOKEN_EXPIRE),
                        DEFAULT_EXPIRE));
            if (checkExpire(rs.getTimestamp("create_date"), expire)) {
              result = FWUserManagerPreRegisterStatus.PRE_REGISTER;
            } else {
              result = FWUserManagerPreRegisterStatus.EXPIRE;
            }
          }
        } else {
          result = FWUserManagerPreRegisterStatus.NONE;
        }
      }
    }
    logger.debug("status={}", result);
    logger.perfEnd("validPreToken", startTime);
    return result;
  }

  private boolean checkExpire(Timestamp createDate, int expire) {
    boolean result = false;
    long tl = createDate.getTime();
    tl += (expire * 60 * 1000); // ミリ秒変換
    Date d = new Date(tl);
    Date now = new Date();
    if (now.compareTo(d) > 0) { // 有効期限切れ
      result = false;
    } else {
      result = true;
    }
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int actualRegister(String preToken) throws SQLException {
    int result = 0;
    long startTime = logger.perfStart("actualRegister");
    String sql =
        "UPDATE fw_user_management SET pre_register = ?, update_date = ? WHERE pre_token = ?";
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      ps.setBoolean(1, false);
      ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      ps.setString(3, preToken);
      result = ps.executeUpdate();
    }
    if (result == 1) {
      String selectSql = "SELECT id FROM fw_user_management WHERE pre_token = ?";
      try (FWPreparedStatement ps = con.prepareStatement(selectSql)) {
        ps.setString(1, preToken);
        try (FWResultSet rs = ps.executeQuery()) {
          rs.next();
          String id = rs.getString("id");
          loginMgr.login(id);
        }
      }
    }

    logger.perfEnd("actualRegister", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public String getMailAddress(String id) throws SQLException {
    String sql = "SELECT mail_address FROM fw_user WHERE id = ?";
    FWConnection con = cm.getConnection();
    String result = null;
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          result = rs.getString("mail_address");
        }
      }
    }
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public String getUserId(String token) throws SQLException {
    String sql = "SELECT * FROM fw_user_passwd_reset WHERE reset_token = ?";
    FWConnection con = cm.getConnection();
    String result = null;
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, token);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          result = rs.getString("id");
        }
      }
    }
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWUserManagerPreRegisterStatus validResetToken(String token) throws SQLException {
    long startTime = logger.perfStart("validResetToken");
    FWConnection con = cm.getConnection();
    FWUserManagerPreRegisterStatus result = null;
    try (FWPreparedStatement ps =
        con.prepareStatement("SELECT * FROM fw_user_passwd_reset WHERE reset_token = ?")) {
      ps.setString(1, token);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int expire =
              Integer.parseInt(
                  FWStringUtil.replaceNullString(fwMsg.get(USER_RESET_TOKEN_EXPIRE),
                      DEFAULT_EXPIRE));
          if (checkExpire(rs.getTimestamp("create_date"), expire)) {
            result = FWUserManagerPreRegisterStatus.PRE_REGISTER;
          } else {
            result = FWUserManagerPreRegisterStatus.EXPIRE;
          }
        } else {
          result = FWUserManagerPreRegisterStatus.NONE;
        }
      }
    }
    logger.perfEnd("validResetToken", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public String initResetPassword(String id) throws SQLException {
    long startTime = logger.perfStart("initResetPassword");
    FWConnection con = cm.getConnection();
    // まず削除しておく
    try (FWPreparedStatement ps =
        con.prepareStatement("DELETE FROM fw_user_passwd_reset WHERE id = ?")) {
      ps.setString(1, id);
      ps.executeUpdate();
    }
    String token = FWInternalUtil.generateToken();
    try (FWPreparedStatement ps =
        con.prepareStatement("INSERT INTO fw_user_passwd_reset (id, reset_token) VALUES (?, ?)")) {
      ps.setString(1, id);
      ps.setString(2, token);
      ps.executeUpdate();
    }
    logger.perfEnd("initResetPassword", startTime);
    return token;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int resetPassword(String id, String password) throws SQLException {
    long startTime = logger.perfStart("resetPassword");
    int r = changePassword(id, password);
    if (r != 0) {
      FWConnection con = cm.getConnection();
      try (FWPreparedStatement ps =
          con.prepareStatement("DELETE FROM fw_user_passwd_reset WHERE id = ?")) {
        ps.setString(1, id);
        ps.executeUpdate();
      }
    }
    logger.perfEnd("resetPassword", startTime);
    return r;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public boolean isPreRegister(String id) throws SQLException {
    String sql = "SELECT * FROM fw_user_management WHERE id = ?";
    FWConnection con = cm.getConnection();
    boolean result = false;
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          result = rs.getBoolean("pre_register");
        }
      }
    }
    return result;
  }
}
