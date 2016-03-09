package com.csframe.config;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.csframe.common.FWConstantCode;
import com.csframe.context.FWContext;
import com.csframe.log.FWLogger;

@ApplicationScoped
@Named("fwMsgResources")
public class FWMessageResourcesImpl implements FWMessageResources {

  @Inject
  private FWContext ctx;

  @Inject
  private FWLogger logger;

  @Override
  public String get(String key) {

    return get(key, ctx.getUser().getLanguage());
  }

  @Override
  public String get(String key, Locale locale) {

    logger.debug("get(). key={}, locale={}", key, locale);

    ResourceBundle rb = ResourceBundle.getBundle(ctx.getApplicationId(), locale);
    if (!rb.containsKey(key)) {
      throw new FWMissingResourceException(FWConstantCode.PROPERTIES_KEY_MISSING);
    } else {
      String value = rb.getString(key);
      logger.debug("get() return. value={}", value);
      return value;
    }
  }

  @Override
  public Set<String> keySet() {

    return keySet(ctx.getUser().getLanguage());
  }

  @Override
  public Set<String> keySet(Locale locale) {

    ResourceBundle rb = ResourceBundle.getBundle(ctx.getApplicationId(), locale);
    return rb.keySet();
  }

  @Override
  public ResourceBundle getBundle() {

    return getBundle(ctx.getUser().getLanguage());
  }

  @Override
  public ResourceBundle getBundle(Locale locale) {
    return ResourceBundle.getBundle(ctx.getApplicationId(), locale);
  }
}
