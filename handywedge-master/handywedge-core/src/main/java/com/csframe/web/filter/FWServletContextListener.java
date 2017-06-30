/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.web.filter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.common.FWStringUtil;
import com.csframe.context.FWApplicationContext;
import com.csframe.log.FWLogger;
import com.csframe.util.FWInternalUtil;

@WebListener
public class FWServletContextListener implements ServletContextListener {

  @Inject
  private FWLogger logger;

  @Inject
  private FWInternalUtil util;

  @Inject
  private FWApplicationContext appCtx;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    logger.info("アプリケーションのデプロイ処理を行います。");
    init(sce);
    util.cacheAPIToken();
    util.cacheRoleAcl();
    util.checkUserManagement();
  }

  private void init(ServletContextEvent sce) {
    try {
      appCtx.setHostName(InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      appCtx.setHostName("unknown");
    }
    String appId = sce.getServletContext().getInitParameter("csf.app_id");
    if (FWStringUtil.isEmpty(appId)) {
      throw new FWRuntimeException(FWConstantCode.NO_APP_ID);
    }
    appCtx.setApplicationId(appId);
    appCtx.setContextPath(sce.getServletContext().getContextPath());
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

}
