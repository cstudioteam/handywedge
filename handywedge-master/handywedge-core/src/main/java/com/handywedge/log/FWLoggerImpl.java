/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import org.slf4j.Logger;
import org.slf4j.Marker;

public class FWLoggerImpl implements FWLogger {

  private Logger logger;

  FWLoggerImpl(Logger logger) {
    this.logger = logger;
  }

  private static final String FW_PREFIX = "com.handywedge";

  private boolean start() {

    if (FWMDC.getCurrentLogName() == null) {
      if (getName().startsWith(FW_PREFIX)) {
        FWMDC.setLogName(FWLogName.FW);
      } else {
        FWMDC.setLogName(FWLogName.AP);
      }
      return true;
    } else {
      return false;
    }
  }

  private void end(boolean remove) {

    if (remove) {
      FWMDC.remove(FWMDC.LOG_NAME);
    }
  }

  /* FW独自 API */

  @Override
  public long perfStart(String signature) {

    FWLogName current = FWMDC.setLogName(FWLogName.PERF);
    try {
      long startTime = System.currentTimeMillis();
      logger.info("{}() start.", signature);
      return startTime;
    } finally {
      FWMDC.setLogName(current);
    }
  }

  @Override
  public void perfEnd(String signature, long startTime) {

    FWLogName current = FWMDC.setLogName(FWLogName.PERF);
    try {
      logger.info("{}() end.\tElapsedTime[{}]ms", signature,
          System.currentTimeMillis() - startTime);
    } finally {
      FWMDC.setLogName(current);
    }
  }

  @Override
  public void respLog(String uri, long startTime) {

    FWLogName current = FWMDC.setLogName(FWLogName.RESP);
    try {
      logger.info("{}\tElapsedTime[{}]ms", uri, System.currentTimeMillis() - startTime);
    } finally {
      FWMDC.setLogName(current);
    }
  }

  /* Logger API */

  @Override
  public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {

    boolean remove = start();
    logger.debug(arg0, arg1, arg2, arg3);
    end(remove);
  }

  @Override
  public void debug(Marker arg0, String arg1, Object... arg2) {

    boolean remove = start();
    logger.debug(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void debug(Marker arg0, String arg1, Object arg2) {

    boolean remove = start();
    logger.debug(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void debug(Marker arg0, String arg1, Throwable arg2) {

    boolean remove = start();
    logger.debug(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void debug(Marker arg0, String arg1) {

    boolean remove = start();
    logger.debug(arg0, arg1);
    end(remove);
  }

  @Override
  public void debug(String arg0, Object arg1, Object arg2) {

    boolean remove = start();
    logger.debug(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void debug(String arg0, Object... arg1) {

    boolean remove = start();
    logger.debug(arg0, arg1);
    end(remove);
  }

  @Override
  public void debug(String arg0, Object arg1) {

    boolean remove = start();
    logger.debug(arg0, arg1);
    end(remove);
  }

  @Override
  public void debug(String arg0, Throwable arg1) {

    boolean remove = start();
    logger.debug(arg0, arg1);
    end(remove);
  }

  @Override
  public void debug(String arg0) {

    boolean remove = start();
    logger.debug(arg0);
    end(remove);
  }

  @Override
  public void error(Marker arg0, String arg1, Object arg2, Object arg3) {

    boolean remove = start();
    logger.error(arg0, arg1, arg2, arg3);
    end(remove);
  }

  @Override
  public void error(Marker arg0, String arg1, Object... arg2) {

    boolean remove = start();
    logger.error(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void error(Marker arg0, String arg1, Object arg2) {

    boolean remove = start();
    logger.error(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void error(Marker arg0, String arg1, Throwable arg2) {

    boolean remove = start();
    logger.error(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void error(Marker arg0, String arg1) {

    boolean remove = start();
    logger.error(arg0, arg1);
    end(remove);
  }

  @Override
  public void error(String arg0, Object arg1, Object arg2) {

    boolean remove = start();
    logger.error(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void error(String arg0, Object... arg1) {

    boolean remove = start();
    logger.error(arg0, arg1);
    end(remove);
  }

  @Override
  public void error(String arg0, Object arg1) {

    boolean remove = start();
    logger.error(arg0, arg1);
    end(remove);
  }

  @Override
  public void error(String arg0, Throwable arg1) {

    boolean remove = start();
    logger.error(arg0, arg1);
    end(remove);
  }

  @Override
  public void error(String arg0) {

    boolean remove = start();
    logger.error(arg0);
    end(remove);
  }

  @Override
  public String getName() {

    return logger.getName();
  }

  @Override
  public void info(Marker arg0, String arg1, Object arg2, Object arg3) {

    boolean remove = start();
    logger.info(arg0, arg1, arg2, arg3);
    end(remove);
  }

  @Override
  public void info(Marker arg0, String arg1, Object... arg2) {

    boolean remove = start();
    logger.info(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void info(Marker arg0, String arg1, Object arg2) {

    boolean remove = start();
    logger.info(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void info(Marker arg0, String arg1, Throwable arg2) {

    boolean remove = start();
    logger.info(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void info(Marker arg0, String arg1) {

    boolean remove = start();
    logger.info(arg0, arg1);
    end(remove);
  }

  @Override
  public void info(String arg0, Object arg1, Object arg2) {

    boolean remove = start();
    logger.info(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void info(String arg0, Object... arg1) {

    boolean remove = start();
    logger.info(arg0, arg1);
    end(remove);
  }

  @Override
  public void info(String arg0, Object arg1) {

    boolean remove = start();
    logger.info(arg0, arg1);
    end(remove);
  }

  @Override
  public void info(String arg0, Throwable arg1) {

    boolean remove = start();
    logger.info(arg0, arg1);
    end(remove);
  }

  @Override
  public void info(String arg0) {

    boolean remove = start();
    logger.info(arg0);
    end(remove);
  }

  @Override
  public boolean isDebugEnabled() {

    return logger.isDebugEnabled();
  }

  @Override
  public boolean isDebugEnabled(Marker arg0) {

    return logger.isDebugEnabled(arg0);
  }

  @Override
  public boolean isErrorEnabled() {

    return logger.isErrorEnabled();
  }

  @Override
  public boolean isErrorEnabled(Marker arg0) {

    return logger.isErrorEnabled(arg0);
  }

  @Override
  public boolean isInfoEnabled() {

    return logger.isInfoEnabled();
  }

  @Override
  public boolean isInfoEnabled(Marker arg0) {

    return logger.isInfoEnabled(arg0);
  }

  @Override
  public boolean isTraceEnabled() {

    return logger.isTraceEnabled();
  }

  @Override
  public boolean isTraceEnabled(Marker arg0) {

    return logger.isTraceEnabled(arg0);
  }

  @Override
  public boolean isWarnEnabled() {

    return logger.isWarnEnabled();
  }

  @Override
  public boolean isWarnEnabled(Marker arg0) {

    return logger.isWarnEnabled(arg0);
  }

  @Override
  public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {

    boolean remove = start();
    logger.trace(arg0, arg1, arg2, arg3);
    end(remove);
  }

  @Override
  public void trace(Marker arg0, String arg1, Object... arg2) {

    boolean remove = start();
    logger.trace(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void trace(Marker arg0, String arg1, Object arg2) {

    boolean remove = start();
    logger.trace(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void trace(Marker arg0, String arg1, Throwable arg2) {

    boolean remove = start();
    logger.trace(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void trace(Marker arg0, String arg1) {

    boolean remove = start();
    logger.trace(arg0, arg1);
    end(remove);
  }

  @Override
  public void trace(String arg0, Object arg1, Object arg2) {

    boolean remove = start();
    logger.trace(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void trace(String arg0, Object... arg1) {

    boolean remove = start();
    logger.trace(arg0, arg1);
    end(remove);
  }

  @Override
  public void trace(String arg0, Object arg1) {

    boolean remove = start();
    logger.trace(arg0, arg1);
    end(remove);
  }

  @Override
  public void trace(String arg0, Throwable arg1) {

    boolean remove = start();
    logger.trace(arg0, arg1);
    end(remove);
  }

  @Override
  public void trace(String arg0) {

    boolean remove = start();
    logger.trace(arg0);
    end(remove);
  }

  @Override
  public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {

    boolean remove = start();
    logger.warn(arg0, arg1, arg2, arg3);
    end(remove);
  }

  @Override
  public void warn(Marker arg0, String arg1, Object... arg2) {

    boolean remove = start();
    logger.warn(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void warn(Marker arg0, String arg1, Object arg2) {

    boolean remove = start();
    logger.warn(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void warn(Marker arg0, String arg1, Throwable arg2) {

    boolean remove = start();
    logger.warn(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void warn(Marker arg0, String arg1) {

    boolean remove = start();
    logger.warn(arg0, arg1);
    end(remove);
  }

  @Override
  public void warn(String arg0, Object arg1, Object arg2) {

    boolean remove = start();
    logger.warn(arg0, arg1, arg2);
    end(remove);
  }

  @Override
  public void warn(String arg0, Object... arg1) {

    boolean remove = start();
    logger.warn(arg0, arg1);
    end(remove);
  }

  @Override
  public void warn(String arg0, Object arg1) {

    boolean remove = start();
    logger.warn(arg0, arg1);
    end(remove);
  }

  @Override
  public void warn(String arg0, Throwable arg1) {

    boolean remove = start();
    logger.warn(arg0, arg1);
    end(remove);
  }

  @Override
  public void warn(String arg0) {

    boolean remove = start();
    logger.warn(arg0);
    end(remove);
  }

}
