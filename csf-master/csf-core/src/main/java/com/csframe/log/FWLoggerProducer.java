package com.csframe.log;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.csframe.log.FWLogger;

@Dependent
public class FWLoggerProducer {

  @Produces
  public FWLogger getLogger(InjectionPoint ip) {

    return FWLoggerFactory.getLogger(ip.getMember().getDeclaringClass());
  }
}
