/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.common;

import java.nio.charset.StandardCharsets;

import com.handywedge.cdi.FWBeanManager;
import com.handywedge.config.FWMessageResources;
import com.handywedge.context.FWContext;

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

    String url = resources.get(FWMessageResources.LOGIN_URL);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String getRegisterUrl() {
    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);

    String url = resources.get(FWMessageResources.REGISTER_URL);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String getPreRegisterUrl() {
    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);

    String url = resources.get(FWMessageResources.PRE_REGISTER_URL);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String getActRegisterSuccessUrl() {
    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);

    String url = resources.get(FWMessageResources.ACT_REGISTER_SUCCESS_REDIRECT);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String getActRegisterFailUrl() {
    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);

    String url = resources.get(FWMessageResources.ACT_REGISTER_FAIL_REDIRECT);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String getResetPasswdSuccessUrl() {
    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);

    String url = resources.get(FWMessageResources.RESET_PASSWD_SUCCESS_REDIRECT);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String getResetPasswdFailUrl() {
    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);

    String url = resources.get(FWMessageResources.RESET_PASSWD_FAIL_REDIRECT);
    if (FWStringUtil.isEmpty(url)) {
      return null;
    } else {
      return concatContext(url);
    }
  }

  public static String concatContext(String baseUrl) {

    FWContext ctx = FWBeanManager.getBean(FWContext.class);
    if (!baseUrl.startsWith("/")) {
      baseUrl = "/" + baseUrl;
    }

    String contextPath = ctx.getContextPath();
    if (contextPath.endsWith("/")) {
      contextPath = contextPath.substring(0, contextPath.length() - 1);
    }
    return contextPath + baseUrl;
  }

  /**
   * コンテキストパスまでのプロトコル付URLを返します。
   * 
   * @return コンテキストパスまでのURL
   */
  public static String getIncludeContextUrl() {

    FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);
    FWContext ctx = FWBeanManager.getBean(FWContext.class);

    String url = resources.get(FWMessageResources.SERVER_ADDR);
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    String contextPath = ctx.getContextPath();
    if (contextPath.endsWith("/")) {
      contextPath = contextPath.substring(0, contextPath.length() - 1);
    }

    return url + contextPath;
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

  public static String trimControlCharset(String src) {

    if (isEmpty(src)) {
      return src;
    }

    // Control Codes 0 <= 001f and 007f
    StringBuilder sb = new StringBuilder();
    for (char c : src.toCharArray()) {
      if (c > 0x1f && c != 0x7f) {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
