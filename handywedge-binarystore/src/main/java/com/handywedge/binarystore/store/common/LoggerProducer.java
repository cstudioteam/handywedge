/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class LoggerProducer {

  @Inject
  InjectionPoint point;

  @Produces
  public Logger getLogger() {
    String loggerName = point.getMember().getDeclaringClass().getName();
    return LoggerFactory.getLogger(loggerName);
  }
}
