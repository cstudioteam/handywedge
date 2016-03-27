/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user.auth.rest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.csframe.cdi.FWBeanManager;
import com.csframe.common.FWConstantCode;
import com.csframe.common.FWException;
import com.csframe.common.FWRuntimeException;
import com.csframe.common.FWStringUtil;
import com.csframe.log.FWLogger;
import com.csframe.log.FWLoggerFactory;
import com.csframe.user.auth.FWLoginManager;

@ApplicationScoped
@Path("/token")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWAPITokenPublisher {

  private FWLoginManager loginMrg;

  private FWLogger logger = FWLoggerFactory.getLogger(FWAPITokenPublisher.class);

  @PostConstruct
  public void init() {
    loginMrg = FWBeanManager.getBean(FWLoginManager.class);
  }

  @POST
  @Path("/pub")
  public Response publish(FWAPITokenRequest request) {
    logger.debug("publish start. {}", request);

    if (FWStringUtil.isEmpty(request.getId()) || FWStringUtil.isEmpty(request.getPassword())) {
      logger.info("publish end. bad request.");
      FWAPITokenResponse res = new FWAPITokenResponse();
      res.setReturn_cd(FWConstantCode.FW_TOKENPUB_INVALID);
      res.setReturn_msg(
          new FWException(String.valueOf(FWConstantCode.FW_TOKENPUB_INVALID)).getMessage());
      return Response.ok(res).build();
    }
    try {
      if (loginMrg.login(request.getId(), request.getPassword())) {
        String token = loginMrg.publishAPIToken(request.getId());
        FWAPITokenResponse res = new FWAPITokenResponse();
        res.setReturn_cd(0);
        res.setToken(token);
        logger.info("publish end. {}", res);
        return Response.ok(res).build();
      } else {
        logger.info("publish end. fail login.");
        FWAPITokenResponse res = new FWAPITokenResponse();
        res.setReturn_cd(FWConstantCode.FW_TOKENPUB_UNAUTHORIZED);
        res.setReturn_msg(
            new FWException(String.valueOf(FWConstantCode.FW_TOKENPUB_UNAUTHORIZED)).getMessage());
        return Response.ok(res).build();
      }
    } catch (FWRuntimeException e) {
      logger.error("APIトークン発行処理でエラーが発生しました。", e);
      FWAPITokenResponse res = new FWAPITokenResponse();
      res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
      res.setReturn_msg(e.getMessage());
      return Response.ok(res).build();
    }
  }

  @DELETE
  @Path("/delete")
  public Response delete(FWAPITokenRequest request) {
    logger.debug("delete start. {}", request);

    if (FWStringUtil.isEmpty(request.getId()) || FWStringUtil.isEmpty(request.getPassword())) {
      logger.info("delete end. bad request.");
      FWAPITokenResponse res = new FWAPITokenResponse();
      res.setReturn_cd(FWConstantCode.FW_TOKENPUB_INVALID);
      res.setReturn_msg(
          new FWException(String.valueOf(FWConstantCode.FW_TOKENPUB_INVALID)).getMessage());
      return Response.ok(res).build();
    }
    try {
      if (loginMrg.login(request.getId(), request.getPassword())) {
        loginMrg.removeAPIToken(request.getId());
        logger.info("delete end.");
        FWAPITokenResponse res = new FWAPITokenResponse();
        res.setReturn_cd(0);
        return Response.ok(res).build();
      } else {
        logger.info("delete end. fail login.");
        FWAPITokenResponse res = new FWAPITokenResponse();
        res.setReturn_cd(FWConstantCode.FW_TOKENPUB_UNAUTHORIZED);
        res.setReturn_msg(
            new FWException(String.valueOf(FWConstantCode.FW_TOKENPUB_UNAUTHORIZED)).getMessage());
        return Response.ok(res).build();
      }
    } catch (FWRuntimeException e) {
      logger.error("APIトークン削除処理でエラーが発生しました。", e);
      FWAPITokenResponse res = new FWAPITokenResponse();
      res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
      res.setReturn_msg(e.getMessage());
      return Response.ok(res).build();
    }
  }
}
