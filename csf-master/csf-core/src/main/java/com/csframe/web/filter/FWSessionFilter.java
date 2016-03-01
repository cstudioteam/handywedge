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
    long filterStart = System.currentTimeMillis();
    context.setRequestId(UUID.randomUUID().toString());
    FWMDC.put(FWMDC.REQUEST_ID, context.getRequestId());
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    final String requestUrl = httpServletRequest.getRequestURI();
    logger.debug("FWSessionFilter start. uri={}", requestUrl);
    if (requestUrl.startsWith(appCtx.getContextPath() + "/javax.faces.resource/")) {
      try {
        chain.doFilter(httpServletRequest, httpServletResponse);
        logger.debug("FWSessionFilter resources return end.");
        return;
      } finally {
        terminate(httpServletRequest);
      }
    }
    // REST APIはRESTフィルターで処理
    if (requestUrl.startsWith(appCtx.getContextPath() + "/csf/rest/")) {
      try {
        chain.doFilter(httpServletRequest, httpServletResponse);
        logger.debug("FWSessionFilter REST API request return end.");
        return;
      } finally {
        logger.respLog(requestUrl, filterStart);
        terminate(httpServletRequest);
      }
    }

    try {
      FWFullUser user = sessionCtx.getUser();
      String loginUrl = FWStringUtil.getLoginUrl();
      if (!requestUrl.equals(loginUrl) && FWStringUtil.isEmpty(user.getId())) {
        logger.debug("ログインされていません。ログインページにリダイレクトします。 url={}", loginUrl);
        httpServletResponse.sendRedirect(loginUrl);
        return;
      }

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
      logger.respLog(requestUrl, filterStart);
      terminate(httpServletRequest);
    }
  }

  private void terminate(HttpServletRequest request) {
    Boolean login = FWThreadLocal.get(FWThreadLocal.LOGIN); // ログイン・ログアウトフラグ
    if (login != null) {
      if (login) {
        request.changeSessionId(); // セッションは維持してIDだけ変更(Session Fixation)
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
