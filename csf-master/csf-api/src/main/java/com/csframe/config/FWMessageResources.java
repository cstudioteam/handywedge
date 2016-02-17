package com.csframe.config;

import java.util.Locale;
import java.util.Set;

public interface FWMessageResources {
  
  String LOGIN_URL = "fw.login.url";

  String get(String key);

  String get(String key, Locale locale);

  Set<String> keySet();

  Set<String> keySet(Locale locale);
}
