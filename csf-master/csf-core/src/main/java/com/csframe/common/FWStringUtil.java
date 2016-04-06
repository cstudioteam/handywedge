/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.common;

import java.nio.charset.StandardCharsets;

import com.csframe.cdi.FWBeanManager;
import com.csframe.config.FWMessageResources;
import com.csframe.context.FWContext;

// TODO 合成文字「"か" + '\u3099'」とかは考慮しない
// 文字コードはUTF-8で統一
public class FWStringUtil {

  public static boolean isEmpty(String src) {

    return src == null || src.trim().length() == 0;
  }

  /**
   * バイト数のチェック。
   * 
   * @param src チェック対象文字列
   * @param maxLength 最大バイト数
   * @return 最大バイト数より大きければtrue
   */
  public static boolean isByteMoreThan(String src, int maxLength) {
    if (src != null) {
      int length = src.getBytes(StandardCharsets.UTF_8).length;
      if (maxLength < length) {
        return true;
      }
    }
    return false;
  }

  /**
   * 文字数のチェック。
   * 
   * @param src チェック対象文字列
   * @param maxLength 最大文字数
   * @return 最大文字数より大きければtrue
   */
  public static boolean isLengthMoreThan(String src, int maxLength) {
    if (src != null) {
      int length = src.length();
      if (maxLength < length) {
        return true;
      }
    }
    return false;
  }

  public static String getLoginUrl() {

    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);
    FWContext ctx = FWBeanManager.getBean(FWContext.class);

    String contextPath = ctx.getContextPath();
    if (contextPath.endsWith("/")) {
      contextPath = contextPath.substring(0, contextPath.length() - 1);
    }
    String loginUrl = resources.get(FWMessageResources.LOGIN_URL);
    if (!loginUrl.startsWith("/")) {
      loginUrl = "/" + loginUrl;
    }
    return contextPath + loginUrl;
  }

  public static String splitBearerToken(String tokenHeader) {
    String token = null;
    String[] bearerToken = tokenHeader.split(" ");
    if (bearerToken.length == 2 && bearerToken[0].equals("Bearer")) {
      token = bearerToken[1];
    }
    return token;
  }

  /**
   * 引数の文字列がnullもしくは空文字の場合、置換文字に置換する。
   *
   * @param src 対象文字列
   * @param replace 置換文字列
   * @return 対象文字列そのまま、もしくは置換文字列
   */
  public static String replaceNullString(String src, String replace) {

    if (isEmpty(src)) {
      return replace;
    } else {
      return src;
    }
  }
}
