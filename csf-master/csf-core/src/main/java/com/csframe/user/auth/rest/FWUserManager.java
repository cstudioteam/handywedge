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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.csframe.log.FWLogger;
import com.csframe.log.FWLoggerFactory;
import com.csframe.rest.FWRESTEmptyResponse;

@ApplicationScoped
@Path("/user")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FWUserManager {

  private FWLogger logger = FWLoggerFactory.getLogger(FWUserManager.class);

  @PostConstruct
  public void init() {}

  @PUT
  @Path("/password")
  public Response changePassword(FWUserManagerRequest request) {

    long startTime = logger.perfStart("changePassword");
    logger.info("args={}", request);
    FWRESTEmptyResponse res = new FWRESTEmptyResponse();
    res.setReturn_cd(0);
    Response r = Response.ok(res).build();
    logger.perfEnd("changePassword", startTime);

    return r;
  }


}
