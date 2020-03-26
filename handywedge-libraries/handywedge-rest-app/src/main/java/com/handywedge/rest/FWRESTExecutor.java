/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest;

import java.io.InputStream;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handywedge.cdi.FWBeanManager;
import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.config.FWMessageResources;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;

@Path("/")
@RequestScoped
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWRESTExecutor {

  private FWLogger logger = FWLoggerFactory.getLogger(FWRESTExecutor.class);

  private Response doPost(Class<?> logicClazz, InputStream in) throws Exception {
    FWRESTController logic = (FWRESTController) FWBeanManager.getBean(logicClazz);
    Method method = logicClazz.getMethod("doPost", FWRESTRequest.class);
    FWRESTRequestClass annotation = method.getAnnotation(FWRESTRequestClass.class);
    Class<?> requestClazz = annotation.value();

    ObjectMapper om = new ObjectMapper();
    FWRESTRequest req = null;
    try {
      req = (FWRESTRequest) om.readValue(in, requestClazz);
    } catch (Exception e) {
      logger.error("リクエスト変換でエラーが発生しました。", e);
      return Response.ok(createUnmarshalError(e.getMessage())).build();
    }
    logger.debug(req.toString());
    FWRESTResponse res = logic.doPost(req);
    logger.debug(res.toString());
    return Response.ok(res).build();
  }

  @POST
  @Path("/{logicClass}")
  public Response doPost(@PathParam("logicClass") String logicClass, InputStream in) {

    logger.info("REST doPost start. class={}", logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doPost(logicClazz, in);
      logger.info("REST doPost end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  @POST
  @Path("/no_token/{logicClass}")
  public Response doPostNoToken(@PathParam("logicClass") String logicClass, InputStream in) {

    logger.info("REST doPostNoToken start. class={}", logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      if (!(logicClazz.newInstance() instanceof FWRESTNoTokenController)) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doPost(logicClazz, in);
      logger.info("REST doPostNoToken end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  private Response doGet(Class<?> logicClazz, String param) {
    FWRESTController logic = (FWRESTController) FWBeanManager.getBean(logicClazz);
    logger.debug("get parameter={}", param);
    FWRESTResponse res = logic.doGet(param);
    logger.debug(res.toString());
    return Response.ok(res).build();
  }

  @GET
  @Path("/{logicClass}")
  public Response doGet(@PathParam("logicClass") String logicClass) {

    return doGet(logicClass, null);
  }

  @GET
  @Path("/{logicClass}/{param}")
  public Response doGet(@PathParam("logicClass") String logicClass,
      @PathParam("param") String param) {

    logger.info("REST doGet start. class={}", logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doGet(logicClazz, param);
      logger.info("REST doGet end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  @GET
  @Path("/no_token/{logicClass}")
  public Response doGetNoToken(@PathParam("logicClass") String logicClass) {

    return doGetNoToken(logicClass, null);
  }

  @GET
  @Path("/no_token/{logicClass}/{param}")
  public Response doGetNoToken(@PathParam("logicClass") String logicClass,
      @PathParam("param") String param) {

    logger.info("REST doGetNoToken start. class={}", logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      if (!(logicClazz.newInstance() instanceof FWRESTNoTokenController)) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doGet(logicClazz, param);
      logger.info("REST doGetNoToken end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  private Response doPut(Class<?> logicClazz, InputStream in) throws Exception {
    FWRESTController logic = (FWRESTController) FWBeanManager.getBean(logicClazz);
    Method method = logicClazz.getMethod("doPut", FWRESTRequest.class);
    FWRESTRequestClass annotation = method.getAnnotation(FWRESTRequestClass.class);
    Class<?> requestClazz = annotation.value();

    ObjectMapper om = new ObjectMapper();
    FWRESTRequest req = null;
    try {
      req = (FWRESTRequest) om.readValue(in, requestClazz);
    } catch (Exception e) {
      logger.error("リクエスト変換でエラーが発生しました。", e);
      return Response.ok(createUnmarshalError(e.getMessage())).build();
    }
    logger.debug(req.toString());
    FWRESTResponse res = logic.doPut(req);
    logger.debug(res.toString());
    return Response.ok(res).build();
  }

  @PUT
  @Path("/{logicClass}")
  public Response doPut(@PathParam("logicClass") String logicClass, InputStream in) {

    logger.info("REST doPut start. class=" + logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doPut(logicClazz, in);
      logger.info("REST doPut end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  @PUT
  @Path("/no_token/{logicClass}")
  public Response doPutNoToken(@PathParam("logicClass") String logicClass, InputStream in) {

    logger.info("REST doPutNoToken start. class=" + logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      if (!(logicClazz.newInstance() instanceof FWRESTNoTokenController)) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doPut(logicClazz, in);
      logger.info("REST doPutNoToken end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  private Response doDelete(Class<?> logicClazz, InputStream in) throws Exception {
    FWRESTController logic = (FWRESTController) FWBeanManager.getBean(logicClazz);
    Method method = logicClazz.getMethod("doDelete", FWRESTRequest.class);
    FWRESTRequestClass annotation = method.getAnnotation(FWRESTRequestClass.class);
    Class<?> requestClazz = annotation.value();

    ObjectMapper om = new ObjectMapper();
    FWRESTRequest req = null;
    try {
      req = (FWRESTRequest) om.readValue(in, requestClazz);
    } catch (Exception e) {
      logger.error("リクエスト変換でエラーが発生しました。", e);
      return Response.ok(createUnmarshalError(e.getMessage())).build();
    }
    logger.debug(req.toString());
    FWRESTResponse res = logic.doDelete(req);
    logger.debug(res.toString());
    return Response.ok(res).build();
  }

  @DELETE
  @Path("/{logicClass}")
  public Response doDelete(@PathParam("logicClass") String logicClass, InputStream in) {

    logger.info("REST doDelete start. class=" + logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doDelete(logicClazz, in);
      logger.info("REST doDelete end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  @DELETE
  @Path("/no_token/{logicClass}")
  public Response doDeleteNoToken(@PathParam("logicClass") String logicClass, InputStream in) {

    logger.info("REST doDeleteNoToken start. class=" + logicClass);
    try {
      Class<?> logicClazz = getLogicClazz(logicClass);
      if (logicClazz == null) {
        return Response.ok(createRoutingError()).build();
      }
      if (!(logicClazz.newInstance() instanceof FWRESTNoTokenController)) {
        return Response.ok(createRoutingError()).build();
      }
      Response res = doDelete(logicClazz, in);
      logger.info("REST doDeleteNoToken end.");
      return res;
    } catch (Exception e) {
      logger.error("予期しないエラーが発生しました。", e);
      return Response.ok(createError(e.getMessage())).build();
    }
  }

  private Class<?> getLogicClazz(String path) throws ClassNotFoundException {

    Class<?> logicClazz = null;
    try {
      logicClazz = Class.forName(path);
    } catch (ClassNotFoundException e) {
    }

    if (logicClazz == null) {
      FWMessageResources config = FWBeanManager.getBean(FWMessageResources.class);
      String logicClazzName = config.get("fw.rest." + path);
      if (!FWStringUtil.isEmpty(logicClazzName)) {
        logicClazz = Class.forName(logicClazzName);// ここでNotFoundは予期しないエラーにしておく
        logger.info("actual logicClass={}", logicClazzName);
      }
    }
    return logicClazz;
  }

  private FWRESTResponse createError(String args) {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_ERROR), args);
    FWRESTResponse res = new FWRESTEmptyResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_ERROR);
    res.setReturn_msg(e.getMessage());
    return res;
  }

  private FWRESTResponse createRoutingError() {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_ROUTING_ERROR));
    FWRESTResponse res = new FWRESTEmptyResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_ROUTING_ERROR);
    res.setReturn_msg(e.getMessage());
    return res;
  }

  private FWRESTResponse createUnmarshalError(String args) {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_UNMARSHAL_ERROR), args);
    FWRESTResponse res = new FWRESTEmptyResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_UNMARSHAL_ERROR);
    res.setReturn_msg(e.getMessage());
    return res;
  }
}
