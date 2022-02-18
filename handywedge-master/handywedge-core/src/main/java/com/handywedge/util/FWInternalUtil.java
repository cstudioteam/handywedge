/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.context.FWApplicationContext;
import com.handywedge.db.FWInternalConnectionManager;
import com.handywedge.log.FWLogger;
import com.handywedge.role.FWRoleAcl;

@ApplicationScoped
public class FWInternalUtil {

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWLogger logger;

  private static final String NO_USE_ROLE_ACL = "fw.no.use.role.acl";
  private static final String NO_USE_USER_MANAGEMENT = "fw.no.use.user.management";


  public void cacheRoleAcl() {
    String use = getResource(NO_USE_ROLE_ACL);
    if (FWStringUtil.isEmpty(use) || Boolean.parseBoolean(use)) {
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
      } catch (SQLException e) {
        logger.debug(e.toString());
        logger.warn("ロールACLテーブルのアクセスでエラーが発生しました。ロールACL機能は無効化されます。");
      }
      logger.perfEnd("cacheRoleAcl", start);
    } else {
      logger.info("ロールACL機能は無効です。");
    }
  }

  /*
   * v.0.4.0からの新機能
   */
  public void checkUserManagement() {
    String use = getResource(NO_USE_USER_MANAGEMENT);
    if (FWStringUtil.isEmpty(use) || Boolean.parseBoolean(use)) {
      long start = logger.perfStart("checkUserManagement");
      try (Connection con = FWInternalConnectionManager.getConnection();
          Statement st = con.createStatement();
          ResultSet rs = st.executeQuery("SELECT count(*) FROM fw_user_management");) {
        rs.next();
        appCtx.setUserManagementEnable(true);
      } catch (SQLException e) {
        logger.debug(e.toString());
        logger.warn("ユーザー管理テーブルが存在しません。");
        appCtx.setUserManagementEnable(false);
      }
      logger.perfEnd("checkUserManagement", start);
    } else {
      logger.info("ユーザー管理テーブルは無効です。");
      appCtx.setUserManagementEnable(false);
    }
  }

  public static String generateToken() {
    try {
      SecureRandom sr = SecureRandom.getInstanceStrong();
      byte[] b = new byte[16];
      sr.nextBytes(b);
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < b.length; i++) {
        buf.append(String.format("%02x", b[i]));
      }
      return buf.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new FWRuntimeException(FWConstantCode.FATAL, e);
    }
  }

  public static String generatePassword(int length, boolean digit, boolean upper, boolean lower,
      boolean symbol) {

    // 生成処理
    StringBuilder result = new StringBuilder();
    // パスワードに使用する文字を格納
    StringBuilder source = new StringBuilder();
    // 数字
    if (digit) {
      for (int i = 0x30; i < 0x3A; i++) {
        source.append((char) i);
      }
    }
    // 記号
    if (symbol) {
      for (int i = 0x21; i < 0x30; i++) {
        source.append((char) i);
      }
    }
    // アルファベット小文字
    if (lower) {
      for (int i = 0x41; i < 0x5b; i++) {
        source.append((char) i);
      }
    }
    // アルファベット大文字
    if (upper) {
      for (int i = 0x61; i < 0x7b; i++) {
        source.append((char) i);
      }
    }

    int sourceLength = source.length();
    Random random = new Random();
    while (result.length() < length) {
      result.append(source.charAt(Math.abs(random.nextInt()) % sourceLength));
    }
    return result.toString();
  }

  private String getResource(String key) {
    ResourceBundle rb = ResourceBundle.getBundle(appCtx.getApplicationId());
    if (!rb.containsKey(key)) {
      return null;
    } else {
      String value = rb.getString(key);
      return value;
    }
  }
}
