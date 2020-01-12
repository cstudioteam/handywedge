/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect;

import java.util.Base64;
import java.util.UUID;

/**
 * ユーティリティークラス。
 *
 * @author takeuchi
 */
public class OICUtil {

  public static byte[] b64UrlDecode(String coded) {

    byte[] bytes = null;

    if (coded != null) {
      bytes = Base64.getUrlDecoder().decode(coded);
    }

    return bytes;
  }

  public static byte[] b64UrlEncode(String str) {

    byte[] bytes = null;

    if (str != null) {
      bytes = Base64.getUrlEncoder().encode(str.getBytes());
    }

    return bytes;
  }

  public static String getUUID() {

    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }

}
