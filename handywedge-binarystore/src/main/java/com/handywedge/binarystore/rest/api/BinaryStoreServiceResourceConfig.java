/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

@ApplicationPath("/store/service")
public class BinaryStoreServiceResourceConfig extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(BinaryStoreServiceController.class);
    classes.add(JacksonFeature.class);
    classes.add(MultiPartFeature.class);
    classes.add(BinaryStoreServiceCrossFilter.class);
    return classes;
  }
}
