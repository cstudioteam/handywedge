package com.handywedge.report.service;

import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/v1")
public class RestService extends ResourceConfig {

  public RestService() {
    packages("com.handywedge.report.service.api.v1");
  }
}
