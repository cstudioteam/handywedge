/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.config.FWMessageResources;
import com.handywedge.context.FWFullContext;
import com.handywedge.log.FWLogger;
import com.handywedge.mail.FWMailCharacterEncoding;
import com.handywedge.mail.FWMailException;
import com.handywedge.mail.FWMailMessage;
import com.handywedge.mail.FWMailTransport;
import com.handywedge.user.FWUserData;
import com.handywedge.user.FWUserManager;
import com.handywedge.util.FWInternalUtil;

@ApplicationScoped
public class FWUserManagerImpl implements FWUserManager {

  @Inject
  private FWUserService service;

  @Inject
  private FWMailTransport mail;

  @Inject
  private FWMessageResources msg;

  @Inject
  private FWFullContext ctx;

  @Inject
  private FWLogger logger;

  private static final String PRE_TEMPLATE = "fw.pre.user.register.mail.template";
  private static final String INIT_RESET_TEMPLATE =
      "fw.user.register.passwd.init.reset.mail.template";
  private static final String RESET_TEMPLATE =
      "fw.user.register.passwd.reset.mail.template";

  private static final String TEMPLATE_VAR_URL = "<%url%>";
  private static final String TEMPLATE_VAR_PASSWORD = "<%password%>";

  @Override
  public boolean register(String id, String password) {
    return register(id, password, null, null);
  }

  @Override
  public boolean register(String id, String password, Integer preRegister, String mailAddress) {
    try {
      int result = service.insert(id, password, preRegister, mailAddress);
      if (result == 1 && preRegister != null && preRegister > 0) {
        String path = msg.get(PRE_TEMPLATE);
        try {
          String template = readTemplate(path);
          String url = FWStringUtil.getIncludeContextUrl();
          url += "/csf/rest/api/user/actual?token=" + ctx.getPreToken();
          String body = template.replace(TEMPLATE_VAR_URL, url);
          String from = msg.get(FWMessageResources.REGISTER_FROM_ADDR);
          String subject = msg.get(FWMessageResources.PRE_REGISTER_SUBJECT);
          sendMail(mailAddress, from, subject, body, isHtml(path));
        } catch (IOException | FWMailException e) {
          logger.warn("メール通知でエラーが発生しました。", e);
          result = 0;
        }
      }
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public boolean actualRegister(String preToken) {
    try {
      int result = service.actualRegister(preToken);
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public boolean update(FWUserData user) {
    try {
      int result = service.update(user);
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public boolean delete(String id) {
    try {
      int result = service.delete(id);
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public boolean changePassword(String id, String password) {
    try {
      int result = service.changePassword(id, password);
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public String initResetPassword(String id) {
    String token = null;
    try {
      String mailAddress = service.getMailAddress(id);
      if (FWStringUtil.isEmpty(mailAddress)) {
        logger.warn("IDが存在しない、もしくはメールアドレスの登録がないユーザーです。ユーザーID={}", id);
        return null;
      }
      if (ctx.isUserManagementEnable()) {
        boolean preRegister = service.isPreRegister(id);
        if (preRegister) {
          logger.warn("仮登録ユーザーです。ユーザーID={}", id);
          return null;
        }
      }
      token = service.initResetPassword(id);
      if (!FWStringUtil.isEmpty(token)) {
        String path = msg.get(INIT_RESET_TEMPLATE);
        try {
          String template = readTemplate(path);
          String url = FWStringUtil.getIncludeContextUrl();
          url += "/csf/rest/api/user/password/reset?token=" + token;
          String body = template.replace(TEMPLATE_VAR_URL, url);
          String from = msg.get(FWMessageResources.REGISTER_FROM_ADDR);
          String subject = msg.get(FWMessageResources.PASSWD_RESET_SUBJECT);
          sendMail(mailAddress, from, subject, body, isHtml(path));
        } catch (IOException | FWMailException e) {
          logger.warn("メール通知でエラーが発生しました。", e);
          token = null;
        }
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    return token;
  }

  @Override
  public String resetPassword(String token) {
    try {
      String id = service.getUserId(token);
      String mailAddress = service.getMailAddress(id);
      String password = FWInternalUtil.generatePassword(8, true, true, true, false);
      int result = service.resetPassword(id, password);
      if (result != 0) {
        String path = msg.get(RESET_TEMPLATE);
        try {
          String template = readTemplate(path);
          String body = template.replace(TEMPLATE_VAR_PASSWORD, password);
          String from = msg.get(FWMessageResources.REGISTER_FROM_ADDR);
          String subject = msg.get(FWMessageResources.PASSWD_RESET_SUBJECT);
          sendMail(mailAddress, from, subject, body, isHtml(path));
        } catch (IOException | FWMailException e) {
          logger.warn("メール通知でエラーが発生しました。", e);
          logger.info("初期化されたパスワード={}", password);
          password = null;
        }
      }
      return password;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  private void sendMail(String mailAddress, String from, String subject, String body, boolean html)
      throws FWMailException {
    FWMailMessage mm = new FWMailMessage();
    mm.setToAddress(new String[] {mailAddress});
    mm.setFromAddress(from);
    mm.setSubject(subject);
    mm.setBody(body);
    mm.setHtmlFlg(html);
    mm.setCharacterEncoding(FWMailCharacterEncoding.UTF_8);
    mail.send(mm);
  }

  private String readTemplate(String path) throws IOException {
    InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\r\n");
      }
      return sb.toString();
    }
  }

  private boolean isHtml(String path) {
    String l = path.toLowerCase();
    return l.endsWith(".html") || l.endsWith(".htm");
  }

}
