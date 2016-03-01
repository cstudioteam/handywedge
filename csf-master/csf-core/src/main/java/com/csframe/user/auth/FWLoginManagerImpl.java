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
import com.csframe.log.FWMDC;
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
      throw new FWRuntimeException(FWConstantCode.FATAL, e);
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

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public String getAPIToken(String id) {
    logger.debug("getAPIToken start.");
    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    FWConnection con = cm.getConnection();
    String token = null;
    try {
      ps = con.prepareStatement("select token from fw_api_token where id = ?");
      ps.setString(1, id);
      rs = ps.executeQuery();
      if (rs.next()) {
        token = rs.getString("token");
      }
      return token;
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

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public boolean authAPIToken(String token) {
    logger.debug("authAPIToken start.");
    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    FWConnection con = cm.getConnection();
    String id = null;
    try {
      ps = con.prepareStatement("select id from fw_api_token where token = ?");
      ps.setString(1, token);
      rs = ps.executeQuery();
      if (rs.next()) {
        id = rs.getString("id");
      } else {
        logger.debug("invalid_token.");
        return false;
      }
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

    setUser(id, con);

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
        String lang = rs.getString("language");
        if (!FWStringUtil.isEmpty(lang)) {
          String country = rs.getString("country");
          user.setLanguage(new Locale.Builder().setLanguage(lang).setRegion(country).build());
        }
        user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
        FWThreadLocal.put(FWThreadLocal.LOGIN, true); // ログインリクエストフラグ。フィルタで処理をする。
        FWMDC.put(FWMDC.USER_ID, user.getId());
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
