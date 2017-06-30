/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.csframe.rest.api.token.FWAPITokenPublisher;
import com.csframe.rest.api.user.FWUserManageController;

@ApplicationPath("/csf/rest/api")
public class FWRESTServices extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(FWAPITokenPublisher.class);
    classes.add(FWUserManageController.class);
    return classes;
  }
}
