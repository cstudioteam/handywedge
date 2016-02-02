package com.csframe.context;

import java.util.Date;

/**
 * FW内部のみ使用します。
 */
public interface FWRequestContext {

  String getRequestId();

  void setRequestId(String requestId);

  String getContextPath();

  void setContextPath(String contextPath);

  Date getRequestStartTime();

  void setRequestStartTime(Date requestStartTime);

}
