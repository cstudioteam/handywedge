package com.csframe.user.auth.rest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.csframe.log.FWLogger;
import com.csframe.log.FWLoggerFactory;
import com.csframe.user.auth.FWAuthException;
import com.csframe.user.auth.FWLoginManager;
import com.csframe.util.FWBeanManager;
import com.csframe.util.FWStringUtil;

@ApplicationScoped
@Path("/api/token")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWAPITokenPublisher {

  private FWLoginManager loginMrg;

  private FWLogger logger = FWLoggerFactory.getLogger(FWAPITokenPublisher.class);

  @PostConstruct
  public void init() {
    System.out.println("poscon");
    loginMrg = FWBeanManager.getBean(FWLoginManager.class);
  }

  @POST
  @Path("/pub")
  public Response publish(FWAPITokenPublishRequest request) throws FWAuthException {
    logger.debug("publish start. {}", request);

    if (FWStringUtil.isEmpty(request.getId()) || FWStringUtil.isEmpty(request.getPassword())) {
      logger.debug("publish end. bad request.");
      return Response.status(Status.BAD_REQUEST).build();
    }

    if (loginMrg.login(request.getId(), request.getPassword())) {
      String token = loginMrg.publishAPIToken(request.getId());
      logger.debug("publish end. ok token={}", token);
      return Response.ok("{'token':'" + token + "'}").build();
    } else {
      logger.debug("publish end. fail login.");
      return Response.status(Status.UNAUTHORIZED).build();
    }
  }
}
