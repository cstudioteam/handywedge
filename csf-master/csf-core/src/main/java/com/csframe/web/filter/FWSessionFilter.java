/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.web.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import javax.faces.FacesException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.common.FWSessionTimeoutException;
import com.csframe.common.FWStringUtil;
import com.csframe.config.FWMessageResources;
import com.csframe.context.FWApplicationContext;
import com.csframe.context.FWFullContext;
import com.csframe.log.FWLogger;
import com.csframe.log.FWMDC;
import com.csframe.role.FWRoleManager;
import com.csframe.user.FWFullUser;
import com.csframe.util.FWThreadLocal;

@WebFilter(filterName = "csf_session_filter")
public class FWSessionFilter implements Filter {

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWFullContext context;

  @Inject
  private FWLogger logger;

  @Inject
  private FWRoleManager roleMgr;

  @Inject
  private FWMessageResources messageResources;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    try {
      appCtx.setHostName(InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      appCtx.setHostName("unknown");
    }
    String appId = filterConfig.getServletContext().getInitParameter("csf.app_id");
    if (FWStringUtil.isEmpty(appId)) {
      throw new FWRuntimeException(FWConstantCode.NO_APP_ID);
    }
    appCtx.setApplicationId(appId);
    appCtx.setContextPath(filterConfig.getServletContext().getContextPath());
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    long filterStart = System.currentTimeMillis();
    context.setRequestId(UUID.randomUUID().toString());
    FWMDC.put(FWMDC.REQUEST_ID, context.getRequestId());
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    // @セキュリティ webサーバーやapサーバーで設定がありそうだがフィルターで念の為に設定
    httpServletResponse.setHeader("X-XSS-Protection", "1; mode=block"); // ブラウザのXSSフィルターを強制的に有効
    httpServletResponse.setHeader("X-Content-Type-Options", "nosniff"); // ブラウザによるcontent-typeの推測無効
    httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN"); // 他ドメインへのコンテンツ埋め込みを拒否
    if ("TRACE".equals(httpServletRequest.getMethod().toUpperCase())) { // traceメソッドを禁止
      httpServletResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      logger.warn("TRACE request. ip={}, user-agent={}", httpServletRequest.getRemoteAddr(),
          httpServletRequest.getHeader("User-Agent"));
      return;
    }

    final String requestUrl = httpServletRequest.getRequestURI();
    context.setRequestUrl(requestUrl);
    logger.debug("FWSessionFilter start. uri={}", requestUrl);
    if (requestUrl.startsWith(context.getContextPath() + "/javax.faces.resource/")) {
      try {
        chain.doFilter(httpServletRequest, httpServletResponse);
        logger.debug("FWSessionFilter resources return end.");
        return;
      } finally {
        terminate(httpServletRequest);
      }
    }
    // REST APIはRESTフィルターで処理
    if (requestUrl.startsWith(context.getContextPath() + "/csf/rest/")) {
      try {
        context.setRest(true);
        chain.doFilter(httpServletRequest, httpServletResponse);
        logger.debug("FWSessionFilter REST API request return end.");
        return;
      } finally {
        logger.respLog(requestUrl, filterStart);
        terminate(httpServletRequest);
      }
    }

    try {
      FWFullUser user = (FWFullUser) context.getUser();
      String loginUrl = FWStringUtil.getLoginUrl();
      if (!isExternalAuth(requestUrl) && FWStringUtil.isEmpty(user.getId())) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
          String cookieName = messageResources.get(FWMessageResources.SESSION_COOKIE_NAME);
          cookieName = FWStringUtil.replaceNullString(cookieName, "JSESSIONID");
          for (Cookie c : cookies) {
            if (cookieName.equalsIgnoreCase(c.getName())) {
              throw new FWSessionTimeoutException(FWConstantCode.SESSION_TIMEOUT);
            }
          }
        }

        // ログイン前にデフォルト設定としてブラウザ設定言語を使用。ログイン処理の中でユーザマスタに登録されている言語で上書き。
        user.setLocale(httpServletRequest.getLocale());
        logger.debug("ログインされていません。ログインページにリダイレクトします。 url={}", loginUrl);
        httpServletResponse.sendRedirect(loginUrl);
        return;
      }
      FWMDC.put(FWMDC.USER_ID, user.getId());

      long start = logger.perfStart("doFilter");
      try {
        if (!isExternalAuth(requestUrl) && !roleMgr.isAccessAllow()) {
          logger.warn("許可されていないURLへアクセスがありました。user_id={}, role={}, url={}",
              context.getUser().getId(), context.getUser().getRole(), context.getRequestUrl());
          httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "このURLへのアクセスは許可されていません。");
          return;
        }
        try {
          chain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
          logger.perfEnd("doFilter", start);
        }
      } finally {
        context.setLastAccessTime(new Date());
      }
    } catch (FWSessionTimeoutException e) {
      logger.warn("FWSessionFilter FWSessionTimeoutException.");
      throw e;
    } catch (Exception e) {
      // TODO JSFの場合にレスポンスがコミット済の場合があるかもしれないので考慮が必要かも
      terminateError(e);
      throw new ServletException(e);
    } finally {
      logger.respLog(requestUrl, filterStart);
      terminate(httpServletRequest);
    }
  }

  private boolean isExternalAuth(String requestUrl) {
    String loginUrl = FWStringUtil.getLoginUrl();
    String registerUrl = FWStringUtil.getRegisterUrl();
    String preRegisterUrl = FWStringUtil.getPreRegisterUrl();
    String actRegisterFailUrl = FWStringUtil.getActRegisterFailUrl();
    String actRegisterSuccessUrl = FWStringUtil.getActRegisterSuccessUrl();
    String resetPasswordFailUrl = FWStringUtil.getResetPasswdFailUrl();
    String resetPasswordSuccessUrl = FWStringUtil.getResetPasswdSuccessUrl();
    return (requestUrl.equals(loginUrl) || requestUrl.equals(registerUrl)
        || requestUrl.equals(preRegisterUrl) || requestUrl.equals(actRegisterFailUrl)
        || requestUrl.equals(actRegisterSuccessUrl) || requestUrl.equals(resetPasswordFailUrl)
        || requestUrl.equals(resetPasswordSuccessUrl));
  }

  private void terminate(HttpServletRequest request) {
    Boolean login = FWThreadLocal.get(FWThreadLocal.LOGIN); // ログイン・ログアウトフラグ
    if (login != null) {
      if (login) {
        request.changeSessionId(); // @セキュリティ セッションは維持してIDだけ変更(Session Fixation)
      } else {
        request.getSession().invalidate();
      }
    }
    FWThreadLocal.destroy();
    FWMDC.clear();
  }

  private void terminateError(Exception e) {
    Throwable cause;
    if (e instanceof FacesException) {
      cause = e.getCause();
    } else {
      cause = e;
    }

    Boolean b = FWThreadLocal.get(FWThreadLocal.INTERCEPTOR_ERROR);
    if (b != null && !b) {
      logger.error("FWSessionFilter Exception.", cause);
    }
  }

  @Override
  public void destroy() {}

}
