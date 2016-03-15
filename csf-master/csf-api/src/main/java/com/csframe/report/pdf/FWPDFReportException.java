/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.report.pdf;

import com.csframe.common.FWRuntimeException;

/**
 * 帳票出力処理で例外が発生した特にスローされるクラスです。
 */
public class FWPDFReportException extends FWRuntimeException {

  private static final long serialVersionUID = 1L;

  public FWPDFReportException(String code, Object... args) {
    super(code, args);
  }

  public FWPDFReportException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }

}
