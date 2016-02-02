package com.csframe.log;

import org.slf4j.LoggerFactory;

import com.csframe.log.FWLogger;

public final class FWLoggerFactory {

  private FWLoggerFactory() {}

  public static FWLogger getLogger(Class<?> clazz) {

    return new FWLoggerImpl(LoggerFactory.getLogger(clazz));
  }
}
