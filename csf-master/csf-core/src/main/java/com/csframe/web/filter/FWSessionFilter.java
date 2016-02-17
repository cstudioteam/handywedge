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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.context.FWApplicationContext;
import com.csframe.context.FWFullContext;
import com.csframe.context.FWSessionContext;
import com.csframe.log.FWLogger;
import com.csframe.log.FWMDC;
import com.csframe.user.FWFullUser;
import com.csframe.util.FWStringUtil;
import com.csframe.util.FWThreadLocal;

@WebFilter(filterName = "csf_session_filter")
public class FWSessionFilter implements Filter {

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWSessionContext sessionCtx;

  @Inject
  private FWFullContext context;

  @Inject
  private FWLogger logger;

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

    logger.debug("FWSessionFilter start.");
    long filterStart = System.currentTimeMillis();
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (httpServletRequest.getRequestURI().contains("/javax.faces.resource/")) {
      chain.doFilter(httpServletRequest, httpServletResponse);
      logger.debug("FWSessionFilter resources return end.");
      return;
    }
    try {
      context.setRequestId(UUID.randomUUID().toString());
      FWMDC.put(FWMDC.REQUEST_ID, context.getRequestId());

      String loginUrl = FWStringUtil.getLoginUrl();
      String requestUrl = httpServletRequest.getRequestURI();
      FWFullUser user = sessionCtx.getUser();
      if (!requestUrl.contains(loginUrl) && FWStringUtil.isEmpty(user.getId())) {
        logger.debug("ログインされていません。ログインページにリダイレクトします。 url={}", loginUrl);
        httpServletResponse.sendRedirect(loginUrl);
        return;
      }
      FWMDC.put(FWMDC.USER_ID, user.getId());

      long start = logger.perfStart("doFilter");
      try {
        try {
          chain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
          logger.perfEnd("doFilter", start);
        }
      } finally {
        context.setLastAccessTime(new Date());
      }
    } catch (Exception e) {
      // TODO JSFの場合にレスポンスがコミット済の場合があるかもしれないので考慮が必要かも
      terminateError(e);
      throw new ServletException(e);
    } finally {
      Boolean login = FWThreadLocal.get(FWThreadLocal.LOGIN); // ログイン・ログアウトフラグ
      if (login != null) {
        if (login) {
          httpServletRequest.changeSessionId(); // セッションは維持してIDだけ変更(Session Fixation)
        } else {
          httpServletRequest.getSession().invalidate();
        }
      }
      logger.respLog(httpServletRequest.getRequestURI(), filterStart);
      FWThreadLocal.destroy();
      FWMDC.clear();
    }
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
  public void destroy() {

  }

}
