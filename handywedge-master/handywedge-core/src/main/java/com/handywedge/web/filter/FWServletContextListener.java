/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.web.filter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.util.Collections;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.context.FWApplicationContext;
import com.handywedge.log.FWLogger;
import com.handywedge.util.FWInternalUtil;

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
    util.cacheRoleAcl();
    util.checkUserManagement();
  }

  private void init(ServletContextEvent sce) {
    try {
      appCtx.setHostName(InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      appCtx.setHostName("unknown");
    }
    String appId = sce.getServletContext().getInitParameter("handywedge.app_id");
    if (FWStringUtil.isEmpty(appId)) {
      throw new FWRuntimeException(FWConstantCode.NO_APP_ID);
    }
    appCtx.setApplicationId(appId);
    appCtx.setContextPath(sce.getServletContext().getContextPath());
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    Collections.list(DriverManager.getDrivers()).forEach(driver -> {
      try {
        DriverManager.deregisterDriver(driver);
      } catch (final Exception e) {
      }
    });
  }

}
