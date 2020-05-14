package com.handywedge.pushnotice.websocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application Lifecycle Listener implementation class PushNoticeListener
 *
 * @param <PingSender>
 *
 */
public class PushNoticeListener implements ServletContextListener {

  protected static final Logger logger = LogManager.getLogger("Listener");

  /**
   * Default constructor.
   */
  public PushNoticeListener() {}

  @Override
  public void contextInitialized(ServletContextEvent event) {

    logger.info("PushNoticeListener contextInitialized start.");
    ExecutorService exec = Executors.newCachedThreadPool();
    PingSender sender = new PingSender();
    exec.submit(sender);
    logger.info("PushNoticeListener PingSender started.");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // TODO 自動生成されたメソッド・スタブ

  }

}
