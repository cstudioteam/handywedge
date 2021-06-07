/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.MDC;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter(filterName = "binarystore_filter")
public class Filter implements jakarta.servlet.Filter {

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
