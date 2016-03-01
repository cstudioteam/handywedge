package com.csframe.context;

import java.util.Map;

public interface FWApplicationContext {

  String getHostName();

  void setHostName(String hostName);

  String getApplicationId();

  void setApplicationId(String applicationId);

  String getContextPath();

  void setContextPath(String contextPath);

  Map<String, String> getTokenMap();

}
