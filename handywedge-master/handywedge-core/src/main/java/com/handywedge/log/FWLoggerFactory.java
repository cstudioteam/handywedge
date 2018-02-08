/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import org.slf4j.LoggerFactory;

import com.handywedge.log.FWLogger;

public final class FWLoggerFactory {

  private FWLoggerFactory() {}

  public static FWLogger getLogger(Class<?> clazz) {

    return new FWLoggerImpl(LoggerFactory.getLogger(clazz));
  }
}
