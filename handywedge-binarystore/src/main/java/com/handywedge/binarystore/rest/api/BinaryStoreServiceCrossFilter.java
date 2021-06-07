/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BinaryStoreServiceCrossFilter implements ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) throws IOException {
    responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    responseContext.getHeaders().add("Access-Control-Allow-Headers",
        "token , x-requested-with, Content-Type,Origin, Accept, authorization");
    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "false");
    responseContext.getHeaders().add("Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
  }
}
