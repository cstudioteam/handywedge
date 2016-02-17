package com.csframe.user.auth;

import java.sql.SQLException;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;

import com.csframe.common.FWConstantCode;
import com.csframe.db.FWConnection;
import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWTransactional;
import com.csframe.log.FWLogger;
import com.csframe.user.FWFullUser;
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

    // TODO 既にログイン済みの場合はそのまま利用？
    if (!FWStringUtil.isEmpty(user.getId())) {
      return true;
    }

    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    String dbPass = null;
    try {
      FWConnection con = cm.getConnection();
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

    if (BCrypt.checkpw(password, dbPass)) {
      try {
        ps = cm.getConnection().prepareStatement("select * from fw_user where id = ?");
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
          logger.debug("login succeed.");
          FWThreadLocal.put(FWThreadLocal.LOGIN, true); // ログインリクエストフラグ
          return true;
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
    } else {
      logger.debug("login failed.");
      return false;
    }
  }

  @Override
  public void logout() {

    FWThreadLocal.put(FWThreadLocal.LOGIN, false); // ログアウトリクエストフラグ
  }

}
