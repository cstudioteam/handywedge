/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.web.filter;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.csframe.log.FWLogger;
import com.csframe.util.FWInternalUtil;

@WebListener
public class FWServletContextListener implements ServletContextListener {

  @Inject
  private FWLogger logger;

  @Inject
  private FWInternalUtil util;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    logger.info("アプリケーションのデプロイ処理を行います。");
    util.cacheAPIToken();
    util.cacheRoleAcl();
    util.checkUserManagement();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

}
