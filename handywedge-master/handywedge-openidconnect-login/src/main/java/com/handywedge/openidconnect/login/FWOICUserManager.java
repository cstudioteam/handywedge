package com.handywedge.openidconnect.login;

import java.sql.SQLException;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWPasswordUtil;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;
import com.handywedge.log.FWLogger;
import com.handywedge.user.auth.FWLoginManager;

@ApplicationScoped
class FWOICUserManager {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWLoginManager loginMgr;

  @Inject
  private FWLogger logger;

  @FWTransactional(dataSourceName = "jdbc/fw")
  public String login(String id, String name, String mailAddress, String provider) throws SQLException {
    String selectSql = "SELECT count(*) cnt FROM fw_user WHERE id = ?";
    boolean insert = false;
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement(selectSql)) {
      ps.setString(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        rs.next();
        int c = rs.getInt("cnt");
        insert = (c == 0);
      }
    }

    // 登録されていないユーザーの場合はユーザー登録
    if (insert) {
      String insertSql = "INSERT INTO fw_user (id, name, mail_address, oic_register) VALUES (?, ?, ?, ?)";
      try (FWPreparedStatement ps = cm.getConnection().prepareStatement(insertSql)) {
        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, mailAddress);
        ps.setString(4, provider);
        int i = ps.executeUpdate();
        if (i != 1) {
          logger.info("OICログインでユーザー登録に失敗しました。");
          throw new FWRuntimeException(FWConstantCode.DB_FATAL);
        }
      }
      insertSql = "INSERT INTO fw_user_passwd (id, passwd) VALUES (?, ?)";
      try (FWPreparedStatement ps = cm.getConnection().prepareStatement(insertSql)) {
        ps.setString(1, id);
        ps.setString(2, FWPasswordUtil.createPasswordHash(UUID.randomUUID().toString()));
        int i = ps.executeUpdate();
        if (i != 1) {
          logger.info("OICログインでユーザー登録に失敗しました。");
          throw new FWRuntimeException(FWConstantCode.DB_FATAL);
        }
      }
    }

    return loginMgr.publishAPIToken(id, true);
  }
}
