package com.csframe.web.filter;

import java.io.IOException;

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

import com.csframe.log.FWLogger;
import com.csframe.log.FWMDC;
import com.csframe.user.FWUser;
import com.csframe.user.auth.FWLoginManager;
import com.csframe.util.FWStringUtil;

@WebFilter(filterName = "csf_rest_filter")
public class FWRESTFilter implements Filter {

  @Inject
  private FWLogger logger;

  @Inject
  private FWLoginManager loginMgr;
  
  @Inject
  private FWUser user;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    logger.debug("FWRESTFilter start.");

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    final String requestUrl = httpServletRequest.getRequestURI();

    if (requestUrl.equals("/csf/rest/api/token/pub")) {
      logger.debug("API Token publish request.");
    } else {
      String tokenHeader = httpServletRequest.getHeader("Authorization");
      if (!FWStringUtil.isEmpty(tokenHeader)) { // トークン認証
        String token = FWStringUtil.splitBearerToken(tokenHeader);
        if (FWStringUtil.isEmpty(token) || !loginMgr.authAPIToken(token)) {
          logger.debug("APIToken Authorization false.");
          httpServletResponse.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
          httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        } else {
          logger.debug("APIToken Authorization true.");
        }
      }
    }
    FWMDC.put(FWMDC.USER_ID, user.getId());
    chain.doFilter(httpServletRequest, httpServletResponse);
  }

  @Override
  public void destroy() {}

}
