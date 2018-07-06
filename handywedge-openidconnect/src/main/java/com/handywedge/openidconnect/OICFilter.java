package com.handywedge.openidconnect;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Open ID ConnectのリクエストをハンドリングするServlet Filter
 * 
 * @author takeuchi
 */
@WebFilter(filterName = "OCIFilter",
        urlPatterns = {"/*"},
        initParams = {
            @WebInitParam(name = "encoding", value = "utf-8")})
public class OICFilter implements javax.servlet.Filter {

    protected static Logger logger = LoggerFactory.getLogger(OICFilter.class);
    
    private OICService service;
    
    /**
     * Default constructor.
     */
    public OICFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * @param request
     * @param response
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        logger.info("doFilter <start>");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        dump(httpServletRequest);
        
        if (httpServletRequest.getMethod().equalsIgnoreCase("GET")) {
            if (httpServletRequest.getServletPath().equalsIgnoreCase(OICProperties.get(OICConst.RP_LOGIN_PATH))) {
                try {
                    service.login(httpServletRequest, httpServletResponse);
                } catch(OICException e) {
                    logger.error("doFilter <end> Login error.", e);
                    throw new ServletException(e);
                }
            } else {
                httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (httpServletRequest.getMethod().equalsIgnoreCase("POST")) {
            if (httpServletRequest.getServletPath().equalsIgnoreCase(OICProperties.get(OICConst.RP_AUTH_PATH))) {
                try {
                    service.auth(httpServletRequest, httpServletResponse);
                } catch(OICException e) {
                    logger.error("doFilter <end> Auth error.", e);
                    throw new ServletException(e);
                }
            } else {
                httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        logger.info("doFilter <end>");
    }

    /**
     * @param fConfig
     * @throws javax.servlet.ServletException
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig fConfig) throws ServletException {

        logger.info("init <start>");
        try {
            service = OICService.getInstance();
        } catch (OICException e) {
            logger.error("Service Class instantion error", e);
            throw new ServletException(e);
        } finally {
            logger.info("init <end>");
        }
    }

    
    private void dump(HttpServletRequest request) {
        
        logger.trace("---------------------------------------------");
        logger.trace("doFilter : " + request.getMethod());
        logger.trace("doFilter : " + request.getContextPath());
        logger.trace("doFilter : " + request.getServletPath());
        logger.trace("doFilter : " + request.getQueryString());
        Enumeration<String> e = request.getHeaderNames();
        while(e.hasMoreElements()) {
            String key = e.nextElement();
            logger.trace("doFilter : Header key=" + key + " val=" + request.getHeader(key));
        }
        Map<String, String[]> m = request.getParameterMap();
        Iterator<String> i = m.keySet().iterator();
        while(i.hasNext()) {
            String key = i.next();
            logger.trace("doFilter : Param key=" + key);
            String[] v = m.get(key);
            if (v != null) {
                for(String s : v) {
                logger.trace("doFilter : Param key=" + key + " val=" + s);
                }
            }
        }
        logger.trace("---------------------------------------------");
    }
}
