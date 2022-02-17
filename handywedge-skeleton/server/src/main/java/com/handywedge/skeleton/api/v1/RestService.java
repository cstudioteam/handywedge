package com.handywedge.skeleton.api.v1;

import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/fw/rest/app/v1")
public class RestService extends ResourceConfig {

  public RestService() {
    packages("com.handywedge.skeleton.api.v1.controller");
  }
}
