/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.log;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Loggerのラッパーインターフェースです。<br>
 * いくつかhandywedge用のメソッドが追加されています。
 *
 * @see Logger
 */
public interface FWLogger {

  /* FW独自 API */
  /**
   * 処理時間を計測したい場合の開始地点で実行します。<br>
   * 戻り値の開始時間を終了地点でperfEndメソッドの引数に渡します。
   * 
   * @param signature 処理時間を計測するメソッド名
   * @return 計測開始時間
   */
  long perfStart(String signature);

  /**
   * 処理時間を計測したい場合の終了地点で実行します。<br>
   * 開始時点で実行したperfStartの戻り値をメソッドの引数に渡します。
   * 
   * @param signature 処理時間を計測するメソッド名
   * @param startTime 計測開始時間
   */
  void perfEnd(String signature, long startTime);

  /**
   * リクエストの処理時間を計測します。<br>
   * アプリケーションでは使用しないで下さい。
   *
   * @param signature 処理時間を計測するメソッド名
   * @param startTime 計測開始時間
   */
  void respLog(String signature, long startTime);

  /* 以下、Logger API */
  // 使いそうなもの以外は隠蔽する？

  String getName();

  boolean isTraceEnabled();

  void trace(String msg);

  void trace(String format, Object arg);

  void trace(String format, Object arg1, Object arg2);

  void trace(String format, Object... arguments);

  void trace(String msg, Throwable t);

  boolean isTraceEnabled(Marker marker);

  void trace(Marker marker, String msg);

  void trace(Marker marker, String format, Object arg);

  void trace(Marker marker, String format, Object arg1, Object arg2);

  void trace(Marker marker, String format, Object... argArray);

  void trace(Marker marker, String msg, Throwable t);

  boolean isDebugEnabled();

  void debug(String msg);

  void debug(String format, Object arg);

  void debug(String format, Object arg1, Object arg2);

  void debug(String format, Object... arguments);

  void debug(String msg, Throwable t);

  boolean isDebugEnabled(Marker marker);

  void debug(Marker marker, String msg);

  void debug(Marker marker, String format, Object arg);

  void debug(Marker marker, String format, Object arg1, Object arg2);

  void debug(Marker marker, String format, Object... arguments);

  void debug(Marker marker, String msg, Throwable t);

  boolean isInfoEnabled();

  void info(String msg);

  void info(String format, Object arg);

  void info(String format, Object arg1, Object arg2);

  void info(String format, Object... arguments);

  void info(String msg, Throwable t);

  boolean isInfoEnabled(Marker marker);

  void info(Marker marker, String msg);

  void info(Marker marker, String format, Object arg);

  void info(Marker marker, String format, Object arg1, Object arg2);

  void info(Marker marker, String format, Object... arguments);

  void info(Marker marker, String msg, Throwable t);

  boolean isWarnEnabled();

  void warn(String msg);

  void warn(String format, Object arg);

  void warn(String format, Object... arguments);

  void warn(String format, Object arg1, Object arg2);

  void warn(String msg, Throwable t);

  boolean isWarnEnabled(Marker marker);

  void warn(Marker marker, String msg);

  void warn(Marker marker, String format, Object arg);

  void warn(Marker marker, String format, Object arg1, Object arg2);

  void warn(Marker marker, String format, Object... arguments);

  void warn(Marker marker, String msg, Throwable t);

  boolean isErrorEnabled();

  void error(String msg);

  void error(String format, Object arg);

  void error(String format, Object arg1, Object arg2);

  void error(String format, Object... arguments);

  void error(String msg, Throwable t);

  boolean isErrorEnabled(Marker marker);

  void error(Marker marker, String msg);

  void error(Marker marker, String format, Object arg);

  void error(Marker marker, String format, Object arg1, Object arg2);

  void error(Marker marker, String format, Object... arguments);

  void error(Marker marker, String msg, Throwable t);

}
