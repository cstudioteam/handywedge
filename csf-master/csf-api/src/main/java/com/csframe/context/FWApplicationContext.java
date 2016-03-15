/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.util.Map;

/**
 * フレームワーク内部で使用するインターフェースです。<br>
 * アプリケーションでは使用しないで下さい。<br>
 * 
 * コンテキスト情報へのアクセスはFWContextインターフェースを使用して下さい。
 * 
 * @see FWContext
 */
public interface FWApplicationContext {

  String getHostName();

  void setHostName(String hostName);

  String getApplicationId();

  void setApplicationId(String applicationId);

  String getContextPath();

  void setContextPath(String contextPath);

  Map<String, String> getTokenMap();

}
