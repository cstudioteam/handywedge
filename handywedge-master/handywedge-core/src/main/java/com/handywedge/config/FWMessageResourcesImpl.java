/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.config;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.handywedge.config.FWMessageResources;
import com.handywedge.context.FWContext;
import com.handywedge.log.FWLogger;

@ApplicationScoped
@Named("fwMsgResources")
public class FWMessageResourcesImpl implements FWMessageResources {

  @Inject
  private FWContext ctx;

  @Inject
  private FWLogger logger;

  @Override
  public String get(String key) {

    return get(key, ctx.getUser().getLocale());
  }

  @Override
  public String get(String key, Locale locale) {

    ResourceBundle rb = ResourceBundle.getBundle(ctx.getApplicationId(), locale);
    if (!rb.containsKey(key)) {
      logger.debug("get() return. key={}, locale={}, value=null", key, locale);
      return null;
    } else {
      String value = rb.getString(key);
      return value;
    }
  }

  @Override
  public Set<String> keySet() {

    return keySet(ctx.getUser().getLocale());
  }

  @Override
  public Set<String> keySet(Locale locale) {

    ResourceBundle rb = ResourceBundle.getBundle(ctx.getApplicationId(), locale);
    return rb.keySet();
  }

  @Override
  public ResourceBundle getBundle() {

    return getBundle(ctx.getUser().getLocale());
  }

  @Override
  public ResourceBundle getBundle(Locale locale) {
    return ResourceBundle.getBundle(ctx.getApplicationId(), locale);
  }
}
