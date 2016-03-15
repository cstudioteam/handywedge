/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.log;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@Dependent
public class FWLoggerProducer {

  @Produces
  public FWLogger getLogger(InjectionPoint ip) {

    return FWLoggerFactory.getLogger(ip.getMember().getDeclaringClass());
  }
}
