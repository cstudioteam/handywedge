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
import javax.ws.rs.core.Response.Status;

import com.csframe.cdi.FWBeanManager;
import com.csframe.log.FWLogger;
import com.csframe.log.FWLoggerFactory;
import com.csframe.user.auth.FWLoginManager;
import com.csframe.util.FWStringUtil;

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
      logger.debug("publish end. bad request.");
      return Response.status(Status.BAD_REQUEST).build();
    }

    // TODO 認証エラーの例外処理を実装する
    if (loginMrg.login(request.getId(), request.getPassword())) {
      String token = loginMrg.publishAPIToken(request.getId());
      logger.debug("publish end. token={}", token);
      return Response.ok("{'token':'" + token + "'}").build();
    } else {
      logger.debug("publish end. fail login.");
      return Response.status(Status.UNAUTHORIZED).build();
    }
  }

  @DELETE
  @Path("/delete")
  public Response delete(FWAPITokenRequest request) {
    logger.debug("delete start. {}", request);

    if (FWStringUtil.isEmpty(request.getId()) || FWStringUtil.isEmpty(request.getPassword())) {
      logger.debug("delete end. bad request.");
      return Response.status(Status.BAD_REQUEST).build();
    }

    if (loginMrg.login(request.getId(), request.getPassword())) {
      loginMrg.removeAPIToken(request.getId());
      logger.debug("delete end.");
      return Response.ok().build();
    } else {
      logger.debug("delete end. fail login.");
      return Response.status(Status.UNAUTHORIZED).build();
    }
  }
}
