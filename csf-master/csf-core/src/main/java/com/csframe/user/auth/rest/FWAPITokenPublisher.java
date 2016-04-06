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
    logger.info("publish start. args={}", request);

    FWAPITokenResponse res = new FWAPITokenResponse();
    try {
      if (FWStringUtil.isEmpty(request.getId()) || FWStringUtil.isEmpty(request.getPassword())) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_TOKENPUB_INVALID));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_TOKENPUB_INVALID);
        res.setReturn_msg(e.getMessage());
      } else {
        if (loginMrg.login(request.getId(), request.getPassword())) {
          String token = loginMrg.publishAPIToken(request.getId());
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
      res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
      res.setReturn_msg(e.getMessage());
    }
    logger.info("publish end. res={}", res);
    return Response.ok(res).build();
  }

  @DELETE
  @Path("/delete")
  public Response delete(FWAPITokenRequest request) {
    logger.info("delete start. args={}", request);
    FWAPITokenResponse res = new FWAPITokenResponse();
    try {
      if (FWStringUtil.isEmpty(request.getId()) || FWStringUtil.isEmpty(request.getPassword())) {
        FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_TOKENPUB_INVALID));
        logger.warn(e.getMessage());
        res.setReturn_cd(FWConstantCode.FW_REST_TOKENPUB_INVALID);
        res.setReturn_msg(e.getMessage());
      } else {
        if (loginMrg.login(request.getId(), request.getPassword())) {
          loginMrg.removeAPIToken(request.getId());
          res.setReturn_cd(0);
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
      res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
      res.setReturn_msg(e.getMessage());
    }
    logger.info("delete end. res={}", res);
    return Response.ok(res).build();
  }
}
