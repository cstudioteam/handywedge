/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest.api.token;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.context.FWRESTContext;
import com.handywedge.log.FWLogger;
import com.handywedge.user.FWInnerUserService;
import com.handywedge.user.FWUser;
import com.handywedge.user.auth.FWLoginManager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWAPITokenPublisher {

  @Inject
  private FWLoginManager loginMrg;

  @Inject
  private FWInnerUserService userService;

  @Inject
  private FWLogger logger;

  @Inject
  private FWRESTContext restCtx;

  @POST
  @Path("/pub")
  public Response publish(FWAPITokenRequest request) {
    logger.info("publish start. args={}", request);

    FWAPITokenResponse res = new FWAPITokenResponse();
    try {
      if (request == null || FWStringUtil.isEmpty(request.getId())
          || FWStringUtil.isEmpty(request.getPassword())) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_TOKENPUB_INVALID));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_TOKENPUB_INVALID);
        res.setReturn_msg(e.getMessage());
      } else {
        if (loginMrg.checkPassword(request.getId(), request.getPassword())) {
          Integer multiple = request.getMultiple();
          boolean multi = (multiple != null && multiple > 0);
          String token = loginMrg.publishAPIToken(request.getId(), multi);
          FWUser user = userService.getUserByToken(token);
          res.getUser().setId(user.getId());
          res.getUser().setName(user.getName());
          res.getUser().setRole(user.getRole());
          res.getUser().setRoleName(user.getRoleName());
          res.setReturn_cd(0);
          res.setToken(token);
        } else {
          FWException e =
              new FWException(String.valueOf(FWConstantCode.FW_REST_TOKENPUB_UNAUTHORIZED));
          logger.warn(e.getMessage());
          res.setReturn_cd(FWConstantCode.FW_REST_TOKENPUB_UNAUTHORIZED);
          res.setReturn_msg(e.getMessage());
        }
      }
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }
    logger.info("publish end. res={}", res);
    return Response.ok(res).build();
  }

  @DELETE
  @Path("/delete")
  public Response delete() {
    logger.info("delete start.");
    FWAPITokenResponse res = new FWAPITokenResponse();
    try {
      loginMrg.removeAPIToken(restCtx.getToken());
      res.setReturn_cd(0);
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      res = createError(e.getMessage());
    }

    logger.info("delete end. res={}", res);
    return Response.ok(res).build();
  }

  /* トークンの有効性はフィルターでチェックされるのでコード0を返すだけ */
  @GET
  @Path("/validate")
  public Response validate() {
    logger.info("validate start. token={}", restCtx.getToken());
    FWAPITokenResponse res = new FWAPITokenResponse();
    res.setReturn_cd(0);
    logger.info("validate end.");
    return Response.ok(res).build();
  }

  private FWAPITokenResponse createError(String args) {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_ERROR), args);
    FWAPITokenResponse res = new FWAPITokenResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
    res.setReturn_msg(e.getMessage());
    return res;
  }
}
