/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
  /** ResourceBundle. */
  private static ResourceBundle resource = null;

  private PropertiesUtil() {}

  private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

  static {
    resource = ResourceBundle.getBundle("BinaryStoreService");
    logger.debug("--- BinaryStoreService.properties ---");
    Enumeration<String> en = resource.getKeys();
    while (en.hasMoreElements()) {
      String key = en.nextElement();
      logger.debug("{}={}", key, resource.getString(key));
    }
  }

  public static String get(String key) {
    if (resource.containsKey(key)) {
      return resource.getString(key);
    } else {
      return null;
    }
  }

}
