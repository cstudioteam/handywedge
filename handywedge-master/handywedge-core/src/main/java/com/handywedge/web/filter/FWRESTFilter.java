/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.web.filter;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.handywedge.common.FWStringUtil;
import com.handywedge.config.FWMessageResources;
import com.handywedge.context.FWFullRESTContext;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWMDC;
import com.handywedge.user.auth.FWLoginManager;
import com.handywedge.util.FWThreadLocal;

@WebFilter(filterName = "handywedge_rest_filter")
public class FWRESTFilter implements Filter {

  @Inject
  private FWLogger logger;

  @Inject
  private FWLoginManager loginMgr;

  @Inject
  private FWFullRESTContext restCtx;

  @Inject
  private FWMessageResources messageResources;

  private boolean initConfig;

  private String[] oicLoginSourceIp = {};

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    long startTime = logger.perfStart("doFilter");
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletRequest.getSession(true);// REST内ではセッションへのアクセスがなくinvalidateが出来ないので念のため作って最後に廃棄
    try {
      final String requestUrl = httpServletRequest.getRequestURI();
      if (!initConfig) { // rbがセッションスコープにアクセスするのでinitではなくこのタイミングで実施
        initConfig = true;
        try {
          String ip = messageResources.get(FWMessageResources.OIC_SOURCE_IP);
          if (!FWStringUtil.isEmpty(ip)) {
            oicLoginSourceIp = ip.split(",");
          }
        } catch (Exception e) {
        }
      }

      if (httpServletRequest.getMethod().equalsIgnoreCase("POST")
          && (requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/token/pub")
              || requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/token/pub/"))) {
        logger.info("API Token publish request.");
      } else if (httpServletRequest.getMethod().equalsIgnoreCase("POST")
          && (requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/oic/login")
              || requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/oic/login/"))) {
        logger.info("OIC API Token publish request.");
        // IP制限
        String ip = httpServletRequest.getRemoteAddr();
        boolean auth = false;
        for (String source : oicLoginSourceIp) {
          if (source.trim().equals(ip)) {
            auth = true;
            break;
          }
        }
        if (!auth) {
          logger.warn("OICログインを許可していない接続元のリクエストです。IP[{}]", ip);
          httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
      } else if (httpServletRequest.getMethod().equalsIgnoreCase("POST")
          && (requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/user")
              || requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/user/"))) {
        logger.info("User register request.");
      } else if (httpServletRequest.getMethod().equalsIgnoreCase("GET")
          && (requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/user/actual"))) {
        logger.info("User actual register request.");
      } else if (httpServletRequest.getMethod().equalsIgnoreCase("POST")
          && (requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/user/password/reset"))
          || requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/user/password/reset/")) {
        logger.info("password reset request.(POST)");
      } else if (httpServletRequest.getMethod().equalsIgnoreCase("GET")
          && requestUrl.equals(restCtx.getContextPath() + "/fw/rest/api/user/password/reset")) {
        logger.info("password reset request.(GET)");
      } else if (requestUrl.startsWith(restCtx.getContextPath() + "/fw/rest/app/no_token/")) {
        logger.info("No Token API request.");
      } else {
        String tokenHeader = httpServletRequest.getHeader("Authorization");
        logger.info("TOKEN:[{}]", tokenHeader);
        if (FWStringUtil.isEmpty(tokenHeader)) {
          Cookie cookies[] = httpServletRequest.getCookies();// HTTPヘッダにトークンがない場合はCookieから取得を試みる
          if (cookies != null) {
            for (Cookie ck : cookies) {
              logger.debug("COOKIE:[{}][{}]", ck.getName(), ck.getValue());
              if (ck.getName().equalsIgnoreCase("Authorization")) {
                tokenHeader = "Bearer " + ck.getValue();
              }
            }
          }
        }
        if (!FWStringUtil.isEmpty(tokenHeader)) { // トークン認証
          String token = FWStringUtil.splitBearerToken(tokenHeader);
          if (FWStringUtil.isEmpty(token) || !loginMgr.authAPIToken(token)) {
            logger.warn("APIToken Authorization false. Authorization={}", tokenHeader);
            httpServletResponse.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
          } else if (!loginMgr.expirationAPIToken(token)) {
            logger.warn("APIToken Authorization false. Authorization={}", tokenHeader);
            httpServletResponse.setHeader("WWW-Authenticate", "Bearer error=\"expired_token\"");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
          } else {
            logger.info("APIToken Authorization true. token={}", token);
          }
        } else {
          logger.warn("APIToken Header nothing.");
          httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
      }
      FWMDC.put(FWMDC.USER_ID, restCtx.getUserId());
      chain.doFilter(httpServletRequest, httpServletResponse);
    } finally {
      logger.perfEnd("doFilter", startTime);
      FWThreadLocal.put(FWThreadLocal.LOGIN, false); // RESTは常にセッション破棄する
    }
  }

  @Override
  public void destroy() {}

}
