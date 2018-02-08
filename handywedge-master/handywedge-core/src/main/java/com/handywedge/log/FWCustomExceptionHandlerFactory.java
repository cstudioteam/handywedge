/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class FWCustomExceptionHandlerFactory extends ExceptionHandlerFactory {

  private ExceptionHandlerFactory parent;

  public FWCustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {

    this.parent = parent;
  }

  @Override
  public ExceptionHandler getExceptionHandler() {

    ExceptionHandler handler = new FWCustomExceptionHandler(parent.getExceptionHandler());

    return handler;
  }
}
