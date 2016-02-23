package com.csframe.user.auth.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("rest")
public class FWRESTServices extends ResourceConfig {

  public FWRESTServices() {
    register(MoxyJsonFeature.class);
    packages("com.csframe.user.auth.rest");
  }
}
