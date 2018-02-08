/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpServletResponse;

import com.handywedge.log.FWLogger;
import com.handywedge.util.FWThreadLocal;

public class FWCustomExceptionHandler extends ExceptionHandlerWrapper {

  private transient FWLogger logger = FWLoggerFactory.getLogger(FWCustomExceptionHandler.class);

  private ExceptionHandler wrapped;

  FWCustomExceptionHandler(ExceptionHandler exception) {

    this.wrapped = exception;
  }

  @Override
  public ExceptionHandler getWrapped() {

    return wrapped;
  }

  @Override
  public void handle() throws FacesException {

    final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
    while (i.hasNext()) {
      ExceptionQueuedEvent event = i.next();
      ExceptionQueuedEventContext context = event.getContext();

      Throwable t = getRootCause(context.getException()).getCause();
      final FacesContext fc = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
      try {
        if (t != null) {
          Object intercept = FWThreadLocal.get(FWThreadLocal.INTERCEPTOR_ERROR);
          if ((intercept != null) && (boolean) intercept) { // ラップされていてisCommittedの値が正確に取得できないのでフラグで判断
            logger.debug("JSF Exception! after interceptor.");
          } else {
            logger.error("JSF Exception!", t);
            if (!response.isCommitted()) {
              response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
              logger.debug("JSF Exception! response comitted");
            }
          }
        }
      } catch (IOException e) {
        throw new FacesException(e);
      } finally {
        i.remove();
      }
    }
    getWrapped().handle();
  }
}
