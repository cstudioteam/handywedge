package com.csframe.util;

import com.csframe.config.FWMessageResources;
import com.csframe.context.FWContext;

public class FWStringUtil {

  public static boolean isEmpty(String src) {

    return src == null || src.trim().length() == 0;
  }

  public static String getLoginUrl() {

    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);
    FWContext ctx = FWBeanManager.getBean(FWContext.class);

    String contextPath = ctx.getContextPath();
    if (contextPath.endsWith("/")) {
      contextPath = contextPath.substring(0, contextPath.length() - 1);
    }
    String loginUrl = resources.get(FWMessageResources.LOGIN_URL);
    if (!loginUrl.startsWith("/")) {
      loginUrl = "/" + loginUrl;
    }
    return contextPath + loginUrl;
  }

  public static String splitBearerToken(String tokenHeader) {
    String token = null;
    String[] bearerToken = tokenHeader.split(" ");
    if (bearerToken.length == 2 && bearerToken[0].equals("Bearer")) {
      token = bearerToken[1];
    }
    return token;
  }
}
