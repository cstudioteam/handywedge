package jp.cstudio.csfw.web.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Locale;
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

import jp.cstudio.csfw.context.FWApplicationContext;
import jp.cstudio.csfw.context.FWFullContext;
import jp.cstudio.csfw.context.FWSessionContext;
import jp.cstudio.csfw.log.FWLogger;
import jp.cstudio.csfw.log.FWMDC;
import jp.cstudio.csfw.user.FWFullUser;
import jp.cstudio.csfw.util.FWThreadLocal;

@WebFilter(filterName = "cfw_session_filter")
public class FWSessionFilter implements Filter {

  @Inject
  private FWApplicationContext appCtx;

  @Inject
  private FWSessionContext sessionCtx;

  // @Inject
  // private FWFullUser user;

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
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    long filterStart = System.currentTimeMillis();
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    try {
      if (httpServletRequest.getRequestURI().contains("/javax.faces.resource/")) {
        chain.doFilter(httpServletRequest, httpServletResponse);
        return;
      }

      context.setContextPath(httpServletRequest.getContextPath());

      // TODO ルール作成
      context.setApplicationId("DEV");
      context.setRequestId(UUID.randomUUID().toString());
      FWMDC.put(FWMDC.REQUEST_ID, context.getRequestId());

      // TODO ユーザー機能がないので取り敢えず固定値
      FWFullUser user = sessionCtx.getUser();
      user.setId("test_id");
      user.setName("テストユーザ");
      user.setLanguage(Locale.JAPAN);

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
      // TODO AFWを倣うとJSFの場合にレスポンスがコミット済の場合があるので考慮が必要かも
      terminateError(e);
      throw new ServletException(e);
    } finally {
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
