package com.handywedge.skeleton.api.v1.controller;

import com.handywedge.log.FWLogger;
import com.handywedge.skeleton.api.v1.model.SkeletonModel;
import com.handywedge.skeleton.db.dao.SkeletonService;
import com.handywedge.skeleton.db.dto.Skeleton;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/Skeleton")
@RequestScoped
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class SkeletonController {

  @Inject
  private SkeletonService service; // データベースアクセスサービス

  @Inject
  private FWLogger logger; // フレームワークロガー

  @GET
  @Path("/{id}")
  public Response getData(@PathParam("id") String id) {
    try {
      logger.debug("パラメーター[{}]", id);
      SkeletonModel result = service.read(Long.parseLong(id));
      logger.debug("取得結果[{}]", result);
      return Response.ok(result).build();
    } catch (Exception e) {
      logger.error("取得サービスでエラーが発生しました。", e);
      return Response.serverError().build();
    }
  }

  @POST
  public Response createData(Skeleton data) {
    try {
      logger.debug("パラメーター[{}]", data);
      SkeletonModel result = service.create(data);
      logger.debug("生成結果[{}]", result);
      return Response.ok(result).build();
    } catch (Exception e) {
      logger.error("生成サービスでエラーが発生しました。", e);
      return Response.serverError().build();
    }
  }

}
