package com.csframe.user.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.db.FWConnection;
import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWTransactional;
import com.csframe.log.FWLogger;
import com.csframe.user.FWFullUser;
import com.csframe.util.FWPasswordUtil;
import com.csframe.util.FWStringUtil;
import com.csframe.util.FWThreadLocal;

@RequestScoped
public class FWLoginManagerImpl implements FWLoginManager {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWFullUser user;

  @Inject
  private FWLogger logger;

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean login(String id, String password) throws FWAuthException {

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
      throw new FWAuthException(FWConstantCode.AUTH_DB_FAIL, e);
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
      try {
        ps = con.prepareStatement("select * from fw_user where id = ?");
        ps.setString(1, id);
        rs = ps.executeQuery();
        if (rs.next()) {
          user.setId(id);
          user.setName(rs.getString("name"));
          String lang = rs.getString("language");
          if (!FWStringUtil.isEmpty(lang)) {
            String country = rs.getString("country");
            user.setLanguage(new Locale.Builder().setLanguage(lang).setRegion(country).build());
          }
          user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
          logger.debug("login succeed.");
          FWThreadLocal.put(FWThreadLocal.LOGIN, true); // ログインリクエストフラグ。フィルタで処理をする。
        } else {
          logger.warn("ユーザー情報が取得できません。");
          return false; // FKがあるため基本的に起こり得ない
        }
      } catch (SQLException e) {
        throw new FWAuthException(FWConstantCode.AUTH_DB_FAIL, e);
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
      logger.debug("login end.");
      return true;
    } else {
      logger.debug("login failed.");
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
    try {
      SecureRandom sr = SecureRandom.getInstanceStrong();
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
    }

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
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.AUTH_DB_FAIL);
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
}
