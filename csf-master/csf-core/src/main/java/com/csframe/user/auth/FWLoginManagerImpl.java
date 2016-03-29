/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWPasswordUtil;
import com.csframe.common.FWRuntimeException;
import com.csframe.common.FWStringUtil;
import com.csframe.context.FWApplicationContext;
import com.csframe.db.FWConnection;
import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWTransactional;
import com.csframe.log.FWLogger;
import com.csframe.user.FWFullUser;
import com.csframe.util.FWThreadLocal;

@RequestScoped
public class FWLoginManagerImpl implements FWLoginManager {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWFullUser user;

  @Inject
  private FWLogger logger;

  @Inject
  private FWApplicationContext appCtx;

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean login(String id, String password) {

    logger.debug("login start.");

    // TODO 既にログイン済みの場合はそのまま利用？
    if (!FWStringUtil.isEmpty(user.getId())) {
      logger.debug("login end.(exist session)");
      return true;
    }

    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    String dbPass = null;
    FWConnection con = cm.getConnection();
    try {
      ps = con.prepareStatement("select * from fw_user_passwd where id = ?");
      ps.setString(1, id);
      rs = ps.executeQuery();
      if (rs.next()) {
        dbPass = rs.getString("passwd");
      } else {
        return false;
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (Exception e) {
      }
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (Exception e) {
      }
    }

    if (FWPasswordUtil.checkPassword(password, dbPass)) {
      setUser(id, con);
      logger.debug("login end.");
      return true;
    } else {
      logger.debug("login failed. no match password.");
      return false;
    }
  }

  @Override
  public void logout() {

    FWThreadLocal.put(FWThreadLocal.LOGIN, false); // ログアウトリクエストフラグ
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public String publishAPIToken(String id) {

    logger.debug("publishAPIToken start.");
    String token = null;
    logger.debug("generate token start.");
    try {
      // 下記で選択されるNativePRNGBlockingでは、Linux環境だとものすごい遅い（30秒以上かかる）
      // SecureRandom sr = SecureRandom.getInstanceStrong();
      SecureRandom sr = SecureRandom.getInstance("NativePRNGNonBlocking");
      logger.debug("token Algorithme={}", sr.getAlgorithm());
      byte[] b = new byte[16];
      sr.nextBytes(b);
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < b.length; i++) {
        buf.append(String.format("%02x", b[i]));
      }
      token = buf.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.error("abort.", e);
      throw new FWRuntimeException(FWConstantCode.FATAL, e);
    }
    logger.debug("generate token end.");

    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    FWConnection con = cm.getConnection();
    try {
      ps = con.prepareStatement("select id from fw_api_token where id = ?");
      ps.setString(1, id);
      rs = ps.executeQuery();
      boolean update = rs.next();
      ps.close();
      ps = null;
      if (update) {
        logger.debug("token update.");
        ps = con
            .prepareStatement("update fw_api_token set token = ?, create_date = ? where id = ?");
        ps.setString(1, token);
        ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        ps.setString(3, id);
        ps.executeUpdate();
      } else {
        logger.debug("token insert.");
        ps = con.prepareStatement("insert into fw_api_token values(?, ?, ?)");
        ps.setString(1, id);
        ps.setString(2, token);
        ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        ps.executeUpdate();
      }
      appCtx.getTokenMap().put(id, token);
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
        if (ps != null) {
          ps.close();
        }
      } catch (Exception e) {
      }
    }
    logger.debug("publishAPIToken end.");
    return token;
  }

  @Override
  public String getAPIToken(String id) {
    logger.debug("getAPIToken start.");
    String token = appCtx.getTokenMap().get(id);
    logger.debug("getAPIToken end. id={}, token={}", id, token);
    return token;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public void removeAPIToken(String id) {

    logger.debug("removeAPIToken start.");
    FWPreparedStatement ps = null;
    FWConnection con = cm.getConnection();
    try {
      ps = con.prepareStatement("delete from fw_api_token where id = ?");
      ps.setString(1, id);
      ps.executeUpdate();
      appCtx.getTokenMap().remove(id);
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL);
    } finally {
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (Exception e) {
      }
    }
    logger.debug("removeAPIToken end.");
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean authAPIToken(String token) {
    logger.debug("authAPIToken start.");

    Map<String, String> tokenMap = appCtx.getTokenMap();
    Set<Entry<String, String>> entrySet = tokenMap.entrySet();
    Iterator<Entry<String, String>> it = entrySet.iterator();

    String id = null;
    while (it.hasNext()) {
      Entry<String, String> e = it.next();
      if (e.getValue().equals(token)) {
        id = e.getKey();
        break;
      }
    }

    if (FWStringUtil.isEmpty(id)) {
      logger.debug("invalid_token.");
      return false;
    }

    setUser(id, cm.getConnection());

    logger.debug("authAPIToken end.");
    return true;
  }

  private void setUser(String id, FWConnection con) {

    // セッションオブジェクトにユーザー情報をセット
    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    try {
      ps = con.prepareStatement("select * from fw_user where id = ?");
      ps.setString(1, id);
      rs = ps.executeQuery();
      if (rs.next()) {
        user.setId(id);
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        String lang = rs.getString("language");
        if (!FWStringUtil.isEmpty(lang)) {
          String country = rs.getString("country");
          user.setLocale(new Locale.Builder().setLanguage(lang).setRegion(country).build());
        }
        user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
        FWThreadLocal.put(FWThreadLocal.LOGIN, true); // ログインリクエストフラグ。フィルタで処理をする。
      } else {
        throw new FWRuntimeException(FWConstantCode.FATAL, "ユーザー情報が取得できません。"); // 基本的に来ないはず
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (Exception e) {
      }
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (Exception e) {
      }
    }

    // 最終ログイン時間更新
    try {
      ps = con.prepareStatement("update fw_user set last_login_date = ? where id = ?");
      ps.setTimestamp(1, user.getLastLoginTime());
      ps.setString(2, user.getId());
      int i = ps.executeUpdate();
      if (i == 1) {
        logger.debug("update 'last_login_date'.");
      } else {
        logger.warn("fail update 'last_login_date'.");
      }
    } catch (SQLException e) {
      logger.warn("fail update 'last_login_date'.", e);
    } finally {
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (Exception e) {
      }
    }
  }
}
