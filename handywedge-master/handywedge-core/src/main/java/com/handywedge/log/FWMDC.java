/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import org.slf4j.MDC;

import com.handywedge.log.FWLogName;

public class FWMDC {

  public static final String LOG_NAME = "logName";
  public static final String REQUEST_ID = "requestId";
  public static final String USER_ID = "userId";

  public static FWLogName setLogName(FWLogName name) {

    FWLogName currenLogName = getCurrentLogName();
    if (name == null) {
      remove(LOG_NAME);
    } else {
      MDC.put(LOG_NAME, name.toString());
    }
    return currenLogName;
  }

  public static FWLogName getCurrentLogName() {

    String logName = MDC.get(LOG_NAME);
    FWLogName retName = null;
    if (logName != null) {
      retName = FWLogName.valueOf(logName);
    }
    return retName;
  }

  public static void clear() {

    MDC.clear();
  }

  public static void put(String key, String value) {

    MDC.put(key, value);
  }

  public static void remove(String key) {

    MDC.remove(key);
  }

}
