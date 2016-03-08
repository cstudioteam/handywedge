package com.csframe.report.pdf;

import com.csframe.common.FWRuntimeException;

public class FWPDFReportException extends FWRuntimeException {

  private static final long serialVersionUID = 1L;

  public FWPDFReportException(String code, Object... args) {
    super(code, args);
  }

  public FWPDFReportException(String code, Throwable cause, Object... args) {
    super(code, cause, args);
  }

}
