/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

  /**
   * 文字列の空チェック
   * 
   * @param str
   * @return
   */
  public static boolean isNullOrEmpty(String str) {
    if (null == str || str.isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * 正規表現でマッチングチェック
   * 
   * @param str
   * @return
   */
  public static boolean regxMatch(String str, String pattern) {
    if (null == str || null == pattern) {
      return false;
    }

    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(str);
    return m.find();
  }

}
