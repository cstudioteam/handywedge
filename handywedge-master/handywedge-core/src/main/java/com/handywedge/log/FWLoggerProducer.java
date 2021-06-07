/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@Dependent
public class FWLoggerProducer {

  @Produces
  public FWLogger getLogger(InjectionPoint ip) {

    return FWLoggerFactory.getLogger(ip.getMember().getDeclaringClass());
  }
}
