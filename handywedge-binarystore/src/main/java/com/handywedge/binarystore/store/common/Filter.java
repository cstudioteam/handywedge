/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.MDC;

@WebFilter(filterName = "binarystore_filter")
public class Filter implements javax.servlet.Filter {

  @Inject
  private Logger logger;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    MDC.put("requestId", UUID.randomUUID().toString());
    long start = System.currentTimeMillis();
    logger.info("Start binarystore request.");
    try {
      chain.doFilter(request, response);
    } finally {
      logger.info("End binarystore request. [{}]ms", System.currentTimeMillis() - start);
      MDC.clear();
    }
  }

  @Override
  public void destroy() {}

}
