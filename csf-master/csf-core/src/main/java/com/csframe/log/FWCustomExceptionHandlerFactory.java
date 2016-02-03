package com.csframe.log;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * FW内部のみ使用します。
 */
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
