/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user.auth;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWPasswordUtil;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.context.FWApplicationContext;
import com.handywedge.context.FWFullRESTContext;
import com.handywedge.db.FWConnection;
import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;
import com.handywedge.log.FWLogger;
import com.handywedge.user.FWFullUser;
import com.handywedge.user.FWUser;
import com.handywedge.user.FWUserImpl;
import com.handywedge.user.auth.FWLoginManager;
import com.handywedge.util.FWInternalUtil;
import com.handywedge.util.FWThreadLocal;

@ApplicationScoped
public class FWLoginManagerImpl implements FWLoginManager {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWFullUser user;

  @Inject
  private FWLogger logger;

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWFullRESTContext restCtx;

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean login(String id, String password) {

    long startTime = logger.perfStart("login");

    if (!FWStringUtil.isEmpty(user.getId())) {
      logger.info("login ok.(exist session)");
      logger.perfEnd("login", startTime);
      return true;
    }

    if (checkPassword(id, password)) {
      // セッションオブジェクトにユーザー情報をセット
      login(id);
      logger.info("login ok.");
      logger.perfEnd("login", startTime);
      return true;
    } else {
      logger.info("login failed.");
      logger.perfEnd("login", startTime);
      return false;
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public void login(String id) {
    FWUser innerUser = getUser(id);
    user.setId(innerUser.getId());
    user.setName(innerUser.getName());
    if (innerUser.getLocale() != null) {
      user.setLocale(innerUser.getLocale());
    }
    user.setRole(innerUser.getRole());
    user.setBeforeLoginTime(innerUser.getLastLoginTime());
    updateLoginTime(id);
    FWThreadLocal.put(FWThreadLocal.LOGIN, true); // ログインリクエストフラグ。フィルタで処理をする
  }

  private String getPassword(String id, FWConnection con) throws SQLException {
    try (FWPreparedStatement ps =
        con.prepareStatement("SELECT * FROM fw_user_passwd WHERE id = ?");) {
      ps.setString(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString("passwd");
        } else {
          return null;
        }
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean checkPassword(String id, String password) {

    long startTime = logger.perfStart("checkPassword");
    String dbPass = null;
    FWConnection con = cm.getConnection();

    try {
      if (appCtx.isUserManagementEnable()) { // 仮登録チェック
        String sql = "SELECT * FROM fw_user_management WHERE id = ? AND pre_register = ?"; // boolの扱いがDBMSで異なりそうなのでJDBC経由で設定する
        try (FWPreparedStatement ps = con.prepareStatement(sql)) {
          ps.setString(1, id);
          ps.setBoolean(2, true);
          try (FWResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
              logger.warn("仮登録ユーザーでのログインです。id={}", id);
              return false;
            }
          }
        }
      }
      dbPass = getPassword(id, con);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    if (dbPass == null) {
      logger.info("user id not found.");
      logger.perfEnd("checkPassword", startTime);
      return false;
    }

    if (FWPasswordUtil.checkPassword(password, dbPass)) {
      logger.info("check ok.");
      logger.perfEnd("checkPassword", startTime);
      return true;
    } else {
      logger.info("check failed. no match password.");
      logger.perfEnd("checkPassword", startTime);
      return false;
    }
  }

  @Override
  public void logout() {

    FWThreadLocal.put(FWThreadLocal.LOGIN, false); // ログアウトリクエストフラグ
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public String publishAPIToken(String id, boolean multiple) {

    long startTime = logger.perfStart("publishAPIToken");
    logger.debug("generate token start.");
    String token = FWInternalUtil.generateToken();
    logger.debug("generate token end.");

    FWConnection con = cm.getConnection();
    try {
      if (!multiple) {
        logger.debug("token delete.");
        try (FWPreparedStatement ps =
            con.prepareStatement("DELETE FROM fw_api_token WHERE id = ?")) {
          ps.setString(1, id);
          ps.executeUpdate();
        }
        List<String> userId = new ArrayList<>();
        userId.add(id);
        appCtx.getTokenMap().values().removeAll(userId);
      }
      logger.debug("token insert.");
      try (FWPreparedStatement ps =
          con.prepareStatement("INSERT INTO fw_api_token (id, token) VALUES(?, ?)")) {
        ps.setString(1, id);
        ps.setString(2, token);
        ps.executeUpdate();
      }
      appCtx.getTokenMap().put(token, id);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.debug("publishAPIToken end. token={}", token);
    updateLoginTime(id);
    logger.perfEnd("publishAPIToken", startTime);
    return token;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public void removeAPIToken(String token) {

    long startTime = logger.perfStart("removeAPIToken");
    FWConnection con = cm.getConnection();
    try (
        FWPreparedStatement ps = con.prepareStatement("DELETE FROM fw_api_token WHERE token = ?")) {
      ps.setString(1, token);
      ps.executeUpdate();
      appCtx.getTokenMap().remove(token);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.perfEnd("removeAPIToken", startTime);
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean authAPIToken(String token) {
    long startTime = logger.perfStart("authAPIToken");

    Map<String, String> tokenMap = appCtx.getTokenMap();
    Set<Entry<String, String>> entrySet = tokenMap.entrySet();
    Iterator<Entry<String, String>> it = entrySet.iterator();

    String id = null;
    while (it.hasNext()) {
      Entry<String, String> e = it.next();
      if (e.getKey().equals(token)) {
        id = e.getValue();
        break;
      }
    }

    if (FWStringUtil.isEmpty(id)) {
      logger.info("invalid_token. token={}", token);
      logger.perfEnd("authAPIToken", startTime);
      return false;
    }
    FWUser innerUser = getUser(id);
    restCtx.setUserId(innerUser.getId());
    restCtx.setUserName(innerUser.getName());
    restCtx.setUserRole(innerUser.getRole());
    if (innerUser.getLocale() != null) {
      restCtx.setUserLocale(innerUser.getLocale());
    }
    restCtx.setToken(token);
    updateLoginTime(id);
    logger.debug("authAPIToken ok.");
    logger.perfEnd("authAPIToken", startTime);
    return true;
  }

  private void updateLoginTime(String id) {
    // 最終ログイン時間更新
    Timestamp t = new Timestamp(System.currentTimeMillis());
    user.setLastLoginTime(t);
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(
        "UPDATE fw_user SET last_login_date = ?, update_date = ? WHERE id = ?");) {
      ps.setTimestamp(1, t);
      ps.setTimestamp(2, t);
      ps.setString(3, id);
      int i = ps.executeUpdate();
      if (i == 1) {
        logger.debug("update 'last_login_date'.");
      } else {
        logger.error("fail update 'last_login_date'.");
        throw new FWRuntimeException(FWConstantCode.DB_FATAL);
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  private FWUser getUser(String id) {
    FWFullUser innerUser = null;
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement("SELECT * FROM fw_user WHERE id = ?")) {
      ps.setString(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          innerUser = new FWUserImpl();
          innerUser.setId(id);
          innerUser.setName(rs.getString("name"));
          innerUser.setRole(rs.getString("role"));
          String lang = rs.getString("language");
          if (!FWStringUtil.isEmpty(lang)) {
            String country = rs.getString("country");
            innerUser.setLocale(new Locale.Builder().setLanguage(lang).setRegion(country).build());
          }
          innerUser.setLastLoginTime(rs.getTimestamp("last_login_date"));

        } else {
          throw new FWRuntimeException(FWConstantCode.FATAL, "ユーザー情報が取得できません。"); // 基本的に来ないはず
        }
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    return innerUser;
  }
}
