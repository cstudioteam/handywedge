/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest.api.user;

import java.net.URI;
import java.net.URISyntaxException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.handywedge.cdi.FWBeanManager;
import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.config.FWMessageResources;
import com.handywedge.context.FWRESTContext;
import com.handywedge.db.FWDatabaseMetaInfo;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;
import com.handywedge.rest.FWRESTEmptyResponse;
import com.handywedge.rest.FWRESTResponse;
import com.handywedge.user.FWUserManager;
import com.handywedge.user.FWUserManagerPreRegisterStatus;
import com.handywedge.user.FWUserService;
import com.handywedge.user.auth.FWLoginManager;

@RequestScoped
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWUserManageController {

  private FWLogger logger = FWLoggerFactory.getLogger(FWUserManageController.class);

  private FWUserManager userMgr;

  private FWLoginManager loginMgr;

  private FWRESTContext ctx;

  private FWUserService service;

  private FWMessageResources fwMsg;

  @PostConstruct
  public void init() {
    userMgr = FWBeanManager.getBean(FWUserManager.class);
    loginMgr = FWBeanManager.getBean(FWLoginManager.class);
    ctx = FWBeanManager.getBean(FWRESTContext.class);
    service = FWBeanManager.getBean(FWUserService.class);
    fwMsg = FWBeanManager.getBean(FWMessageResources.class);
  }

  @PUT
  @Path("/password")
  public Response changePassword(FWUserManagerRequest request) {

    logger.info("changePassword start. args={}", request);
    FWRESTResponse res = new FWRESTEmptyResponse();
    try {
      if (request == null || FWStringUtil.isEmpty(request.getCurrent_password())
          || FWStringUtil.isEmpty(request.getNew_password())) {
        FWException e =
            new FWException(String.valueOf(FWConstantCode.FW_REST_USER_CHANGE_PASSWD_INVALID));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_CHANGE_PASSWD_INVALID);
        res.setReturn_msg(e.getMessage());
      } else if (!loginMgr.checkPassword(ctx.getUserId(), request.getCurrent_password())) {
        FWException e =
            new FWException(String.valueOf(FWConstantCode.FW_REST_USER_CHANGE_PASSWD_UNAUTHORIZED));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_CHANGE_PASSWD_UNAUTHORIZED);
        res.setReturn_msg(e.getMessage());
      } else {
        boolean result = userMgr.changePassword(ctx.getUserId(), request.getNew_password());
        if (result) {
          res.setReturn_cd(0);
        } else {
          res = createError("予期しないエラーが発生しました。");
          logger.error(res.toString());
        }
      }
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }
    logger.info("changePassword end. res={}", res);
    return Response.ok(res).build();
  }

  @POST
  @Path("/password/reset")
  public Response resetPassword(FWUserManagerRequest request) {

    logger.info("resetPassword start. args={}", request);
    FWRESTResponse res = new FWRESTEmptyResponse();
    try {
      if (request == null || FWStringUtil.isEmpty(request.getId())) {
        FWException e =
            new FWException(String.valueOf(FWConstantCode.FW_REST_USER_RESET_PASSWD_ID_EMPTY));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_RESET_PASSWD_ID_EMPTY);
        res.setReturn_msg(e.getMessage());
      } else {
        String result = userMgr.initResetPassword(request.getId());
        if (!FWStringUtil.isEmpty(result)) {
          res.setReturn_cd(0);
        } else {
          res = createError("予期しないエラーが発生しました。");
          logger.error(res.toString());
        }
      }
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }
    logger.info("resetPassword end. res={}", res);
    return Response.ok(res).build();
  }

  @GET
  @Path("/password/reset")
  public Response resetPassword(@QueryParam("token") String token) {

    logger.info("resetPassword start. token={}", token);
    FWRESTResponse res = new FWRESTEmptyResponse();
    String redirect = null;
    try {
      if (FWStringUtil.isEmpty(token)) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_USER_TOKEN_EMPTY));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_TOKEN_EMPTY);
        res.setReturn_msg(e.getMessage());
      } else {
        FWUserManagerPreRegisterStatus preRegisterStatus =
            service.validResetToken(token);
        if (preRegisterStatus == FWUserManagerPreRegisterStatus.NONE) {
          redirect = FWStringUtil.getResetPasswdFailUrl();
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_INVALID));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_INVALID);
          res.setReturn_msg(e.getMessage());
        } else if (preRegisterStatus == FWUserManagerPreRegisterStatus.EXPIRE) {
          redirect = FWStringUtil.getResetPasswdFailUrl();
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_EXPIRE));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_EXPIRE);
          res.setReturn_msg(e.getMessage());
        } else { // 初期化トークン有効
          String password = userMgr.resetPassword(token);
          if (!FWStringUtil.isEmpty(password)) {
            redirect = FWStringUtil.getResetPasswdSuccessUrl();
            res.setReturn_cd(0);
          } else {
            redirect = FWStringUtil.getResetPasswdFailUrl();
            FWException e =
                new FWException(String.valueOf(FWConstantCode.FW_REST_ERROR));
            logger.error(e.getMessage());
            res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
            res.setReturn_msg(e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }
    URI uri = null;
    if (!FWStringUtil.isEmpty(redirect)) {
      String url = fwMsg.get(FWMessageResources.SERVER_ADDR);
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      url += redirect;
      try {
        uri = new URI(url);
      } catch (URISyntaxException e) {
        logger.warn("リダイレクトURLの生成に失敗しました。", e);
      }
    }
    logger.info("resetPassword end. res={}", res);
    if (uri != null) {
      return Response.seeOther(uri).build();
    } else {
      return Response.ok(res).build();
    }
  }

  @POST
  @Path("/")
  public Response register(FWUserManagerRequest request) {
    logger.info("register start. args={}", request);

    FWRESTResponse res = new FWRESTEmptyResponse();
    try {
      if (request == null || FWStringUtil.isEmpty(request.getId())
          || FWStringUtil.isEmpty(request.getPassword())) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_TOKENPUB_INVALID));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_TOKENPUB_INVALID);
        res.setReturn_msg(e.getMessage());
      } else if (FWStringUtil.isLengthMoreThan(request.getId(),
          FWDatabaseMetaInfo.getUserIdLength())) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_USER_REG_ID_INVALID),
            FWDatabaseMetaInfo.getUserIdLength());
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_REG_ID_INVALID);
        res.setReturn_msg(e.getMessage());
      } else if (request.getPre_register() != null && request.getPre_register() > 0
          && FWStringUtil.isEmpty(request.getMail_address())) {
        FWException e =
            new FWException(String.valueOf(FWConstantCode.FW_REST_USER_PRE_REG_MAIL_EMPTY));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_PRE_REG_MAIL_EMPTY);
        res.setReturn_msg(e.getMessage());
      } else {
        boolean result =
            userMgr.register(request.getId(), request.getPassword(), request.getPre_register(),
                request.getMail_address());
        if (result) {
          res.setReturn_cd(0);
        } else {
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_REG_ID_EXISTS));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_REG_ID_EXISTS);
          res.setReturn_msg(e.getMessage());
        }
      }
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }
    logger.info("register end. res={}", res);
    return Response.ok(res).build();
  }

  @GET
  @Path("/actual")
  public Response actualRegister(@QueryParam("token") String token) {
    logger.info("actualRegister start. token={}", token);

    FWRESTResponse res = new FWRESTEmptyResponse();
    String redirect = null;
    try {
      if (FWStringUtil.isEmpty(token)) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_USER_TOKEN_EMPTY));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_USER_TOKEN_EMPTY);
        res.setReturn_msg(e.getMessage());
      } else {
        FWUserManagerPreRegisterStatus preRegisterStatus =
            service.validPreToken(token);
        if (preRegisterStatus == FWUserManagerPreRegisterStatus.NONE) {
          redirect = FWStringUtil.getActRegisterFailUrl();
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_INVALID));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_INVALID);
          res.setReturn_msg(e.getMessage());
        } else if (preRegisterStatus == FWUserManagerPreRegisterStatus.EXPIRE) {
          redirect = FWStringUtil.getActRegisterFailUrl();
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_EXPIRE));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_EXPIRE);
          res.setReturn_msg(e.getMessage());
        } else if (preRegisterStatus == FWUserManagerPreRegisterStatus.REGISTER) {
          redirect = FWStringUtil.getActRegisterFailUrl();
          FWException e =
              new FWException(
                  String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_REGISTERED));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_REGISTERED);
          res.setReturn_msg(e.getMessage());
        } else { // 仮登録状態
          boolean result = userMgr.actualRegister(token);
          if (result) {
            redirect = FWStringUtil.getActRegisterSuccessUrl();
            res.setReturn_cd(0);
          } else {
            redirect = FWStringUtil.getActRegisterFailUrl();
            FWException e =
                new FWException(String.valueOf(FWConstantCode.FW_REST_ERROR));
            logger.error(e.getMessage());
            res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
            res.setReturn_msg(e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }
    URI uri = null;
    if (!FWStringUtil.isEmpty(redirect)) {
      String url = fwMsg.get(FWMessageResources.SERVER_ADDR);
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      url += redirect;
      try {
        uri = new URI(url);
      } catch (URISyntaxException e) {
        logger.warn("リダイレクトURLの生成に失敗しました。", e);
      }
    }
    logger.info("actualRegister end. res={}", res);
    if (uri != null) {
      return Response.seeOther(uri).build();
    } else {
      return Response.ok(res).build();
    }
  }

  private FWRESTResponse createError(String args) {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_ERROR), args);
    FWRESTResponse res = new FWRESTEmptyResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
    res.setReturn_msg(e.getMessage());
    return res;
  }
}
