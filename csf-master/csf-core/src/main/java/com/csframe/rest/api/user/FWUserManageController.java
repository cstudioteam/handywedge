/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest.api.user;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.csframe.cdi.FWBeanManager;
import com.csframe.common.FWConstantCode;
import com.csframe.common.FWException;
import com.csframe.common.FWStringUtil;
import com.csframe.context.FWFullContext;
import com.csframe.context.FWRESTContext;
import com.csframe.db.FWDatabaseMetaInfo;
import com.csframe.log.FWLogger;
import com.csframe.log.FWLoggerFactory;
import com.csframe.rest.FWRESTEmptyResponse;
import com.csframe.rest.FWRESTResponse;
import com.csframe.user.FWUserManager;
import com.csframe.user.FWUserManagerPreRegisterStatus;
import com.csframe.user.FWUserService;
import com.csframe.user.auth.FWLoginManager;

@RequestScoped
@Path("/user")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWUserManageController {

  private FWLogger logger = FWLoggerFactory.getLogger(FWUserManageController.class);

  private FWUserManager userMgr;

  private FWLoginManager loginMgr;

  private FWRESTContext ctx;

  private FWFullContext fwCtx;

  private FWUserService service;

  @PostConstruct
  public void init() {
    userMgr = FWBeanManager.getBean(FWUserManager.class);
    loginMgr = FWBeanManager.getBean(FWLoginManager.class);
    ctx = FWBeanManager.getBean(FWRESTContext.class);
    fwCtx = FWBeanManager.getBean(FWFullContext.class);
    service = FWBeanManager.getBean(FWUserService.class);
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
  @Path("/")
  public Response register(FWUserManagerRequest request) {
    logger.info("register start. args={}", request);

    FWRESTResponse res = new FWUserManagerResponse();
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
      } else {
        boolean result =
            userMgr.register(request.getId(), request.getPassword(), request.getPre_register());
        if (result) {
          res.setReturn_cd(0);
          if (!FWStringUtil.isEmpty(fwCtx.getPreToken())) {
            FWUserManagerResponse r = (FWUserManagerResponse) res;
            r.setPreToken(fwCtx.getPreToken());
          }
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

  @POST
  @Path("/actual")
  public Response actualRegister(FWUserManagerRequest request) {
    logger.info("actualRegister start. args={}", request);

    FWRESTResponse res = new FWRESTEmptyResponse();
    try {
      if (request == null || FWStringUtil.isEmpty(request.getPre_token())) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_TOKENPUB_INVALID));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_TOKENPUB_INVALID);
        res.setReturn_msg(e.getMessage());
      } else {
        FWUserManagerPreRegisterStatus preRegisterStatus =
            service.validPreToken(request.getPre_token());
        if (preRegisterStatus == FWUserManagerPreRegisterStatus.NONE) {
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_INVALID));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_INVALID);
          res.setReturn_msg(e.getMessage());
        } else if (preRegisterStatus == FWUserManagerPreRegisterStatus.EXPIRE) {
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_EXPIRE));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_EXPIRE);
          res.setReturn_msg(e.getMessage());
        } else if (preRegisterStatus == FWUserManagerPreRegisterStatus.REGISTER) {
          FWException e =
              new FWException(
                  String.valueOf(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_REGISTERED));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_USER_ACTUAL_REG_TOKEN_REGISTERED);
          res.setReturn_msg(e.getMessage());
        } else { // 仮登録状態
          boolean result = userMgr.actualRegister(request.getPre_token());
          if (result) {
            res.setReturn_cd(0);
          } else {
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
    logger.info("actualRegister end. res={}", res);
    return Response.ok(res).build();
  }

  private FWRESTResponse createError(String args) {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_ERROR), args);
    FWRESTResponse res = new FWRESTEmptyResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
    res.setReturn_msg(e.getMessage());
    return res;
  }
}
